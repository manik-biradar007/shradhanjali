package com.ereader.ripcardmaker.ui.home

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Gavel
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Policy
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ereader.ripcardmaker.app
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardLanguage
import com.ereader.ripcardmaker.text.LanguagePacks
import com.ereader.ripcardmaker.text.UiStrings
import com.ereader.ripcardmaker.ui.components.CardThumbnail
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onOpenCard: (String) -> Unit) {
    val context = LocalContext.current
    val app = context.app
    val scope = rememberCoroutineScope()

    val savedLanguage by app.prefs.language.collectAsState(initial = null)
    val language = savedLanguage ?: remember { deviceLanguage() }
    val ui = LanguagePacks.of(language).ui
    val drafts by app.drafts.drafts.collectAsState()

    var menuOpen by remember { mutableStateOf(false) }
    var toDelete by remember { mutableStateOf<CardDraft?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ui.appName) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
                actions = {
                    IconButton(onClick = { menuOpen = true }) { Icon(Icons.Filled.MoreVert, contentDescription = null) }
                    HomeMenu(ui, menuOpen, onDismiss = { menuOpen = false }, context = context)
                },
            )
        },
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(150.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = padding.calculateTopPadding(), bottom = padding.calculateBottomPadding() + 24.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize(),
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                HomeHeader(
                    ui = ui,
                    language = language,
                    onLanguage = { scope.launch { app.prefs.setLanguage(it) } },
                    onCreate = {
                        scope.launch {
                            app.prefs.setLanguage(language)
                            onOpenCard(app.drafts.create(language).id)
                        }
                    },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                Text(ui.myCards, style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top = 8.dp))
            }

            if (drafts.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Text(
                        ui.noCardsYet,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 24.dp),
                    )
                }
            } else {
                items(drafts, key = { it.id }) { draft ->
                    DraftTile(
                        draft = draft,
                        ui = ui,
                        onOpen = { onOpenCard(draft.id) },
                        onDuplicate = { scope.launch { app.drafts.duplicate(draft.id) } },
                        onDelete = { toDelete = draft },
                    )
                }
            }
        }
    }

    toDelete?.let { draft ->
        AlertDialog(
            onDismissRequest = { toDelete = null },
            title = { Text(ui.deleteTitle) },
            text = { Text(ui.deleteMessage) },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { app.drafts.delete(draft.id) }
                    toDelete = null
                }) { Text(ui.delete) }
            },
            dismissButton = { TextButton(onClick = { toDelete = null }) { Text(ui.cancel) } },
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HomeHeader(ui: UiStrings, language: CardLanguage, onLanguage: (CardLanguage) -> Unit, onCreate: () -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(20.dp)) {
            Text(ui.tagline, style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(16.dp))
            Text(ui.cardLanguage, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
            Spacer(Modifier.height(4.dp))
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CardLanguage.entries.forEach { lang ->
                    FilterChip(
                        selected = lang == language,
                        onClick = { onLanguage(lang) },
                        label = { Text(lang.nativeName) },
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            Button(onClick = onCreate, modifier = Modifier.fillMaxWidth().height(52.dp)) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.size(8.dp))
                Text(ui.createCard)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DraftTile(draft: CardDraft, ui: UiStrings, onOpen: () -> Unit, onDuplicate: () -> Unit, onDelete: () -> Unit) {
    var menu by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onOpen, onLongClick = { menu = true }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        CardThumbnail(draft, Modifier.fillMaxWidth().padding(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 12.dp, bottom = 4.dp)) {
            Column(Modifier.weight(1f)) {
                Text(
                    draft.deceased.fullName.ifEmpty { ui.untitled },
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    "${draft.language.nativeName} · ${DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(draft.updatedAt))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Box {
                IconButton(onClick = { menu = true }) { Icon(Icons.Filled.MoreVert, contentDescription = null) }
                DropdownMenu(expanded = menu, onDismissRequest = { menu = false }) {
                    DropdownMenuItem(
                        text = { Text(ui.duplicate) },
                        leadingIcon = { Icon(Icons.Outlined.ContentCopy, null) },
                        onClick = { menu = false; onDuplicate() },
                    )
                    DropdownMenuItem(
                        text = { Text(ui.delete) },
                        leadingIcon = { Icon(Icons.Outlined.Delete, null) },
                        onClick = { menu = false; onDelete() },
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeMenu(ui: UiStrings, expanded: Boolean, onDismiss: () -> Unit, context: Context) {
    val activity = context as? Activity
    DropdownMenu(expanded = expanded, onDismissRequest = onDismiss) {
        DropdownMenuItem(text = { Text(ui.shareApp) }, leadingIcon = { Icon(Icons.Outlined.Share, null) }, onClick = {
            onDismiss()
            val link = "https://play.google.com/store/apps/details?id=${context.packageName}"
            val send = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, "${ui.shareAppMessage}\n$link")
            }
            context.startActivity(Intent.createChooser(send, ui.shareApp))
        })
        DropdownMenuItem(text = { Text(ui.rateApp) }, leadingIcon = { Icon(Icons.Outlined.StarRate, null) }, onClick = {
            onDismiss()
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}")))
            } catch (e: ActivityNotFoundException) {
                openUrl(context, "https://play.google.com/store/apps/details?id=${context.packageName}")
            }
        })
        HorizontalDivider()
        PolicyItem(ui.privacyPolicy, Icons.Outlined.PrivacyTip, "privacy-policy", context, onDismiss)
        PolicyItem(ui.terms, Icons.Outlined.Gavel, "terms-and-conditions", context, onDismiss)
        PolicyItem(ui.refundPolicy, Icons.Outlined.Policy, "cancellation-and-refund-policy", context, onDismiss)
        PolicyItem(ui.pricingPolicy, Icons.Outlined.Receipt, "pricing-policy", context, onDismiss)
        PolicyItem(ui.shippingPolicy, Icons.Outlined.LocalShipping, "shipping-policy", context, onDismiss)
        if (activity != null && context.app.consent.privacyOptionsRequired) {
            DropdownMenuItem(text = { Text(ui.privacyChoices) }, leadingIcon = { Icon(Icons.Outlined.Description, null) }, onClick = {
                onDismiss()
                context.app.consent.showPrivacyOptions(activity)
            })
        }
    }
}

@Composable
private fun PolicyItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, slug: String, context: Context, onDismiss: () -> Unit) {
    DropdownMenuItem(text = { Text(label) }, leadingIcon = { Icon(icon, null) }, onClick = {
        onDismiss()
        openUrl(context, "https://freebiodatamaker.com/$slug-for-shradhanjali-frame-maker/")
    })
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (_: ActivityNotFoundException) {
    }
}

private fun deviceLanguage(): CardLanguage =
    CardLanguage.fromCode(Locale.getDefault().language) ?: CardLanguage.ENGLISH
