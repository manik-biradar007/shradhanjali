package com.ereader.ripcardmaker.render

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.RectF
import android.graphics.Typeface
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.LruCache
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.model.Photo
import com.ereader.ripcardmaker.text.CardContent
import com.ereader.ripcardmaker.text.CardTextBuilder
import kotlin.math.max
import kotlin.math.min

/** Tappable parts of the card, used by the editor preview to jump to the right tab. */
enum class CardRegion { TITLE, PHOTO, NAME, MESSAGE, MEETING, FAMILY }

/**
 * Draws a card onto a Canvas in card coordinates (format width × height).
 * The same code paints the live preview and the exported image, so they always match.
 */
class CardRenderer(context: Context) {

    private val resources = context.applicationContext.resources

    private val cache = object : LruCache<String, Bitmap>(
        (Runtime.getRuntime().maxMemory() / 8).toInt().coerceAtMost(64 * 1024 * 1024)
    ) {
        override fun sizeOf(key: String, value: Bitmap) = value.byteCount
    }

    // ── Bitmaps ─────────────────────────────────────────────────────────────

    fun assetBitmap(asset: Asset?): Bitmap? {
        asset ?: return null
        val key = "a:${asset.id}"
        cache.get(key)?.let { return it }
        val opts = BitmapFactory.Options().apply { inScaled = false }
        val bmp = runCatching { BitmapFactory.decodeResource(resources, asset.resId, opts) }.getOrNull() ?: return null
        cache.put(key, bmp)
        return bmp
    }

    fun photoBitmap(photo: Photo): Bitmap? {
        val path = photo.path ?: return null
        val key = "p:$path:${photo.version}"
        cache.get(key)?.let { return it }
        val bmp = runCatching { BitmapFactory.decodeFile(path) }.getOrNull() ?: return null
        cache.put(key, bmp)
        return bmp
    }

    // ── Public API ──────────────────────────────────────────────────────────

    /** Renders [draft] to a new bitmap [widthPx] wide (height follows the format). */
    fun renderBitmap(draft: CardDraft, widthPx: Int = draft.format.widthPx): Bitmap {
        val fmt = draft.format
        val scale = widthPx.toFloat() / fmt.widthPx
        val bmp = Bitmap.createBitmap(widthPx, (fmt.heightPx * scale).toInt(), Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.scale(scale, scale)
        draw(canvas, draft, CardTextBuilder.build(draft), editorMode = false)
        return bmp
    }

    /**
     * Draws the card in card coordinates. Returns the regions that were drawn.
     * In [editorMode] a placeholder is shown where the photo goes if none is chosen.
     */
    fun draw(canvas: Canvas, draft: CardDraft, content: CardContent, editorMode: Boolean): Map<CardRegion, RectF> {
        val w = draft.format.widthPx.toFloat()
        val h = draft.format.heightPx.toFloat()
        val style = draft.style

        // 1. Background colour, pattern and frame.
        canvas.drawColor(style.backgroundColor)
        val full = RectF(0f, 0f, w, h)
        assetBitmap(AssetCatalog.find(style.patternId))?.let { canvas.drawBitmap(it, null, full, bitmapPaint) }
        assetBitmap(AssetCatalog.find(style.frameId))?.let { frame ->
            val p = Paint(bitmapPaint)
            style.frameTint?.let { p.colorFilter = PorterDuffColorFilter(it, PorterDuff.Mode.SRC_IN) }
            canvas.drawBitmap(frame, null, full, p)
        }

        // 2. Content, auto-fitted between the frame margins.
        val photo = photoBitmap(draft.photo)
        val ring = assetBitmap(AssetCatalog.find(style.ringId))
        val diya = assetBitmap(AssetCatalog.find(style.diyaId))
        val showPhotoSlot = photo != null || editorMode

        val left = w * SIDE_MARGIN
        val contentW = w - 2 * left
        val top = h * TOP_MARGIN
        val availH = h - top - h * BOTTOM_MARGIN

        val serif = draft.language == CardLanguage.ENGLISH
        val textScale = style.textScale.coerceIn(0.7f, 1.4f)

        fun measure(s: Float) = Blocks(content, s, textScale, contentW, style.textColor, style.accentColor, serif,
            showPhotoSlot, diya != null)

        // Largest scale (≤ 1) at which everything fits.
        var lo = 0.35f
        var hi = 1f
        var blocks = measure(hi)
        if (blocks.totalHeight > availH) {
            repeat(14) {
                val mid = (lo + hi) / 2
                if (measure(mid).totalHeight <= availH) lo = mid else hi = mid
            }
            blocks = measure(lo)
        }

        // Spread leftover space: extra gaps (capped), then centre vertically.
        val leftover = availH - blocks.totalHeight
        val gapCount = blocks.items.size - 1
        val extraGap = if (gapCount > 0) min(leftover / (gapCount + 2), 70f * blocks.s) else 0f
        var y = top + (leftover - extraGap * gapCount) / 2

        val boxStroke = Paint(Paint.ANTI_ALIAS_FLAG).apply { this.style = Paint.Style.STROKE }
        val regions = linkedMapOf<CardRegion, RectF>()
        blocks.items.forEachIndexed { index, item ->
            val rect = RectF(left, y, left + contentW, y + item.height)
            when (item) {
                is Item.Text -> {
                    canvas.save()
                    canvas.translate(left + (contentW - item.layout.width) / 2, y)
                    item.layout.draw(canvas)
                    canvas.restore()
                }
                is Item.PhotoRow -> drawPhotoRow(canvas, rect, item, photo, ring, diya, draft.photo, style.accentColor, editorMode, boxStroke)
                is Item.Boxed -> {
                    val box = RectF(left + item.inset, y, left + contentW - item.inset, y + item.height)
                    canvas.drawRoundRect(box, item.radius, item.radius, boxFill)
                    boxStroke.color = withAlpha(style.accentColor, 110)
                    boxStroke.strokeWidth = 2.5f
                    canvas.drawRoundRect(box, item.radius, item.radius, boxStroke)
                    var iy = y + item.pad
                    for (l in item.layouts) {
                        canvas.save()
                        canvas.translate(left + (contentW - l.width) / 2, iy)
                        l.draw(canvas)
                        canvas.restore()
                        iy += l.height + item.innerGap
                    }
                }
            }
            regions[item.region] = regions[item.region]?.apply { union(rect) } ?: rect
            y += item.height + if (index < gapCount) blocks.gap + extraGap else 0f
        }
        return regions
    }

    // ── Photo row ───────────────────────────────────────────────────────────

    private fun drawPhotoRow(
        canvas: Canvas, rect: RectF, item: Item.PhotoRow,
        photo: Bitmap?, ring: Bitmap?, diya: Bitmap?, photoState: Photo, accent: Int, editorMode: Boolean, boxStroke: Paint,
    ) {
        val cx = rect.centerX()
        val cy = rect.top + item.ringSize / 2

        if (item.hasPhotoSlot) {
            val diameter = item.ringSize * if (ring != null) 0.74f else 0.84f
            val radius = diameter / 2
            if (photo != null) {
                drawPhotoInCircle(canvas, photo, cx, cy, radius, photoState)
            } else if (editorMode) {
                drawPlaceholder(canvas, cx, cy, radius)
            }
            if (ring != null) {
                val half = item.ringSize / 2
                canvas.drawBitmap(ring, null, RectF(cx - half, cy - half, cx + half, cy + half), bitmapPaint)
            } else {
                boxStroke.color = accent
                boxStroke.strokeWidth = max(3f, diameter * 0.012f)
                canvas.drawCircle(cx, cy, radius, boxStroke)
            }
        }

        if (diya != null) {
            val dh = item.diyaHeight
            val dw = dh * diya.width / diya.height
            if (item.hasPhotoSlot) {
                val bottom = rect.top + item.ringSize * 0.96f
                val ringLeft = cx - item.ringSize / 2
                val ringRight = cx + item.ringSize / 2
                val leftCx = (rect.left + ringLeft) / 2
                val rightCx = (ringRight + rect.right) / 2
                canvas.drawBitmap(diya, null, RectF(leftCx - dw / 2, bottom - dh, leftCx + dw / 2, bottom), bitmapPaint)
                canvas.drawBitmap(diya, null, RectF(rightCx - dw / 2, bottom - dh, rightCx + dw / 2, bottom), bitmapPaint)
            } else {
                canvas.drawBitmap(diya, null, RectF(cx - dw / 2, rect.top, cx + dw / 2, rect.top + dh), bitmapPaint)
            }
        }
    }

    private fun drawPhotoInCircle(canvas: Canvas, photo: Bitmap, cx: Float, cy: Float, radius: Float, p: Photo) {
        val d = radius * 2
        val base = d / min(photo.width, photo.height)
        val scale = base * p.scale.coerceIn(1f, 5f)
        val m = Matrix().apply {
            postTranslate(-photo.width / 2f, -photo.height / 2f)
            postScale(scale, scale)
            postTranslate(cx + p.offsetX * d, cy + p.offsetY * d)
        }
        val clip = Path().apply { addCircle(cx, cy, radius, Path.Direction.CW) }
        canvas.save()
        canvas.clipPath(clip)
        canvas.drawColor(Color.WHITE)
        canvas.drawBitmap(photo, m, bitmapPaint)
        canvas.restore()
    }

    private fun drawPlaceholder(canvas: Canvas, cx: Float, cy: Float, r: Float) {
        val p = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xFFE7E2DA.toInt() }
        canvas.drawCircle(cx, cy, r, p)
        p.color = 0xFFC4BBAE.toInt()
        canvas.save()
        canvas.clipPath(Path().apply { addCircle(cx, cy, r, Path.Direction.CW) })
        canvas.drawCircle(cx, cy - r * 0.18f, r * 0.30f, p)
        canvas.drawOval(RectF(cx - r * 0.62f, cy + r * 0.22f, cx + r * 0.62f, cy + r * 1.25f), p)
        canvas.restore()
    }

    // ── Layout model ────────────────────────────────────────────────────────

    private sealed class Item(val region: CardRegion) {
        abstract val height: Float

        class Text(region: CardRegion, val layout: StaticLayout) : Item(region) {
            override val height get() = layout.height.toFloat()
        }

        class PhotoRow(val ringSize: Float, val diyaHeight: Float, val hasPhotoSlot: Boolean) : Item(CardRegion.PHOTO) {
            override val height get() = if (hasPhotoSlot) ringSize else diyaHeight
        }

        class Boxed(
            region: CardRegion, val layouts: List<StaticLayout>,
            val pad: Float, val innerGap: Float, val inset: Float, val radius: Float,
        ) : Item(region) {
            override val height get() = pad * 2 + layouts.sumOf { it.height }.toFloat() + innerGap * (layouts.size - 1)
        }
    }

    private class Blocks(
        content: CardContent, val s: Float, t: Float, contentW: Float,
        textColor: Int, accent: Int, serif: Boolean, photoSlot: Boolean, hasDiya: Boolean,
    ) {
        val gap = 26f * s
        val items = mutableListOf<Item>()
        val totalHeight: Float

        init {
            val family = if (serif) Typeface.SERIF else Typeface.DEFAULT
            val bold = Typeface.create(family, Typeface.BOLD)
            val regular = Typeface.create(family, Typeface.NORMAL)
            val ts = s * t
            val width = contentW.toInt()

            items += Item.Text(CardRegion.TITLE, layout(content.title, width, 60f * ts, accent, bold))

            if (photoSlot || hasDiya) {
                val ring = 390f * s
                items += Item.PhotoRow(ring, ring * 0.55f, photoSlot)
            }

            items += Item.Text(CardRegion.NAME, layout(content.name, width, 56f * ts, accent, bold))
            items += Item.Text(CardRegion.MESSAGE, layout(content.message, width, 39f * ts, textColor, regular, 1.2f))

            if (content.meetingTitle != null) {
                val pad = 22f * s
                val inset = 10f * s
                val inner = (width - 2 * (pad + inset)).toInt()
                val layouts = buildList {
                    add(layout(content.meetingTitle, inner, 43f * ts, accent, bold))
                    content.meetingLines.forEach { add(layout(it, inner, 37f * ts, textColor, regular, 1.15f)) }
                }
                items += Item.Boxed(CardRegion.MEETING, layouts, pad, 8f * s, inset, 26f * s)
            }

            if (content.familyTitle != null) {
                items += Item.Text(CardRegion.FAMILY, layout(content.familyTitle, width, 43f * ts, accent, bold))
                items += Item.Text(
                    CardRegion.FAMILY,
                    layout(content.familyLines.joinToString("\n"), width, 35f * ts, textColor, regular, 1.2f),
                )
            }

            totalHeight = items.sumOf { it.height.toDouble() }.toFloat() + gap * (items.size - 1)
        }

        @SuppressLint("WrongConstant") // Layout.BREAK_STRATEGY_* equals LineBreaker.* (API 29+); lint only knows the latter.
        private fun layout(
            text: String, width: Int, size: Float, color: Int, typeface: Typeface, spacing: Float = 1.08f,
        ): StaticLayout {
            val paint = TextPaint(Paint.ANTI_ALIAS_FLAG or Paint.SUBPIXEL_TEXT_FLAG).apply {
                textSize = size
                this.color = color
                this.typeface = typeface
            }
            val measured = text.lines().maxOf { StaticLayout.getDesiredWidth(it, paint) }
            val w = min(width.toFloat(), measured + 2f).toInt().coerceAtLeast(1)
            return StaticLayout.Builder.obtain(text, 0, text.length, paint, w)
                .setAlignment(Layout.Alignment.ALIGN_CENTER)
                .setLineSpacing(0f, spacing)
                .setIncludePad(false)
                .setBreakStrategy(Layout.BREAK_STRATEGY_BALANCED)
                .build()
        }
    }

    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
    private val boxFill = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = 0xB3FFFFFF.toInt() }

    private fun withAlpha(color: Int, alpha: Int) = (color and 0x00FFFFFF) or (alpha shl 24)

    companion object {
        /** Margins as fractions of the card size, clear of every frame's border. */
        const val SIDE_MARGIN = 0.115f
        const val TOP_MARGIN = 0.065f
        const val BOTTOM_MARGIN = 0.06f

        @Volatile private var instance: CardRenderer? = null
        fun get(context: Context): CardRenderer =
            instance ?: synchronized(this) { instance ?: CardRenderer(context).also { instance = it } }
    }
}
