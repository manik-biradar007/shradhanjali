package com.ereader.ripcardmaker.ui.preview

import android.Manifest
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.ereader.ripcardmaker.app
import com.ereader.ripcardmaker.export.CardExporter
import com.ereader.ripcardmaker.model.CardDraft
import com.ereader.ripcardmaker.model.CardFormat
import com.ereader.ripcardmaker.text.LanguagePacks
import com.ereader.ripcardmaker.ui.components.CardPreview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreviewScreen(draftId: String, onBack: () -> Unit, onDone: () -> Unit) {
    val context = LocalContext.current
    val activity = context as Activity
    val app = context.app
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }

    val drafts by app.drafts.drafts.collectAsState()
    val draft = drafts.firstOrNull { it.id == draftId }
    if (draft == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val ui = LanguagePacks.of(draft.language).ui
    val ready = draft.deceased.fullName.isNotEmpty()

    var busy by remember { mutableStateOf(false) }
    var askUnlock by remember { mutableStateOf(false) }

    fun update(t: (CardDraft) -> CardDraft) {
        app.appScope.launch {
            val latest = app.drafts.get(draftId) ?: return@launch
            app.drafts.save(t(latest), touch = false)
        }
    }

    fun save() {
        busy = true
        scope.launch {
            val ok = app.exporter.saveToGallery(draft)
            busy = false
            if (ok) update { it.copy(lastExportedAt = System.currentTimeMillis()) }
            snackbar.showSnackbar(if (ok) ui.saved else ui.saveFailed)
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) save() else scope.launch { snackbar.showSnackbar(ui.permissionNeeded) }
    }

    fun onSave() {
        val needsPermission = app.exporter.needsLegacyPermission &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
        if (needsPermission) permissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE) else save()
    }

    fun share(targetPackage: String?) {
        busy = true
        scope.launch {
            val intent = app.exporter.shareIntent(draft, targetPackage)
            busy = false
            if (intent == null) {
                snackbar.showSnackbar(ui.saveFailed)
                return@launch
            }
            try {
                context.startActivity(if (targetPackage == null) Intent.createChooser(intent, ui.share) else intent)
                update { it.copy(lastExportedAt = System.currentTimeMillis()) }
            } catch (e: ActivityNotFoundException) {
                snackbar.showSnackbar(ui.whatsappMissing)
            }
        }
    }

    fun finish() = app.ads.maybeShowInterstitial(activity) { onDone() }

    BackHandler { onBack() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBar(
                title = { Text(ui.preview) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null) }
                },
                actions = {
                    TextButton(onClick = ::finish) {
                        Icon(Icons.Filled.Check, null)
                        Spacer(Modifier.width(4.dp))
                        Text(ui.done)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(Modifier.weight(1f).fillMaxWidth().padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                CardPreview(draft = draft, editorMode = false)
                if (busy) CircularProgressIndicator()
            }

            SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                val formats = listOf(CardFormat.STANDARD to ui.formatStandard, CardFormat.STATUS to ui.formatStatus)
                formats.forEachIndexed { i, (format, label) ->
                    SegmentedButton(
                        selected = draft.format == format,
                        onClick = {
                            when {
                                format == CardFormat.STATUS && !draft.statusUnlocked -> askUnlock = true
                                else -> update { it.copy(format = format) }
                            }
                        },
                        shape = SegmentedButtonDefaults.itemShape(i, formats.size),
                        icon = {
                            if (format == CardFormat.STATUS && !draft.statusUnlocked) {
                                Icon(Icons.Outlined.Lock, null, Modifier.size(18.dp))
                            } else {
                                SegmentedButtonDefaults.Icon(draft.format == format)
                            }
                        },
                    ) { Text(label, maxLines = 1) }
                }
            }

            Spacer(Modifier.height(12.dp))

            if (!ready) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(12.dp)) {
                        Text(ui.nameRequired, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = onBack) {
                            Icon(Icons.Outlined.Edit, null)
                            Spacer(Modifier.width(4.dp))
                            Text(ui.edit)
                        }
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = ::onSave, enabled = ready && !busy, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Download, null)
                    Spacer(Modifier.width(6.dp))
                    Text(ui.saveToGallery, maxLines = 1)
                }
                FilledTonalButton(onClick = { share(null) }, enabled = ready && !busy, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Outlined.Share, null)
                    Spacer(Modifier.width(6.dp))
                    Text(ui.share, maxLines = 1)
                }
            }
            OutlinedButton(
                onClick = { share(CardExporter.WHATSAPP) },
                enabled = ready && !busy,
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp),
            ) {
                Icon(Icons.AutoMirrored.Outlined.Chat, null)
                Spacer(Modifier.width(6.dp))
                Text(ui.whatsapp)
            }
        }
    }

    if (askUnlock) {
        AlertDialog(
            onDismissRequest = { askUnlock = false },
            icon = { Icon(Icons.Outlined.Lock, null) },
            title = { Text(ui.unlockTitle) },
            text = { Text(ui.unlockMessage) },
            confirmButton = {
                TextButton(onClick = {
                    askUnlock = false
                    app.ads.showRewarded(activity) {
                        update { it.copy(statusUnlocked = true, format = CardFormat.STATUS) }
                    }
                }) { Text(ui.watchAd) }
            },
            dismissButton = { TextButton(onClick = { askUnlock = false }) { Text(ui.cancel) } },
        )
    }
}
