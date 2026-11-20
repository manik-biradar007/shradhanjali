package com.ereader.ripcardmaker.ui.editor

import android.app.Application
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.isImeVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.ereader.ripcardmaker.render.CardRegion
import com.ereader.ripcardmaker.text.LanguagePacks
import com.ereader.ripcardmaker.ui.components.CardPreview

private enum class EditorTab { PHOTO, DETAILS, DESIGN, TEXT }

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditorScreen(draftId: String, onBack: () -> Unit, onPreview: () -> Unit) {
    val application = LocalContext.current.applicationContext as Application
    val vm: EditorViewModel = viewModel(
        key = draftId,
        factory = viewModelFactory { initializer { EditorViewModel(application, draftId) } },
    )
    val draft by vm.draft.collectAsStateWithLifecycle()
    val importing by vm.importingPhoto.collectAsStateWithLifecycle()
    var tab by rememberSaveable { mutableIntStateOf(EditorTab.DETAILS.ordinal) }

    DisposableEffect(Unit) { onDispose { vm.flush() } }
    BackHandler { vm.flush(); onBack() }

    val current = draft
    if (current == null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }
    val pack = LanguagePacks.of(current.language)
    val ui = pack.ui

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        current.deceased.fullName.ifEmpty { ui.newCard },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { vm.flush(); onBack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    Button(onClick = { vm.flush(); onPreview() }, modifier = Modifier.padding(end = 8.dp)) {
                        Text(ui.preview)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background),
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .imePadding()
        ) {
            // The card shrinks while the keyboard is open so the field being typed stays visible.
            val previewHeight = if (WindowInsets.isImeVisible) 150.dp else 330.dp
            Box(
                Modifier.fillMaxWidth().height(previewHeight).padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                CardPreview(
                    draft = current,
                    editorMode = true,
                    onRegionTap = { region ->
                        tab = when (region) {
                            CardRegion.PHOTO -> EditorTab.PHOTO
                            CardRegion.TITLE -> EditorTab.TEXT
                            else -> EditorTab.DETAILS
                        }.ordinal
                    },
                )
            }

            val tabs = listOf(
                Triple(EditorTab.PHOTO, ui.tabPhoto, Icons.Outlined.Image),
                Triple(EditorTab.DETAILS, ui.tabDetails, Icons.Outlined.Person),
                Triple(EditorTab.DESIGN, ui.tabStyle, Icons.Outlined.Palette),
                Triple(EditorTab.TEXT, ui.tabText, Icons.Outlined.TextFields),
            )
            PrimaryTabRow(selectedTabIndex = tab, containerColor = MaterialTheme.colorScheme.background) {
                tabs.forEach { (t, label, icon) ->
                    Tab(
                        selected = tab == t.ordinal,
                        onClick = { tab = t.ordinal },
                        text = { Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                        icon = { Icon(icon, contentDescription = null) },
                    )
                }
            }

            Column(
                Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(bottom = padding.calculateBottomPadding() + 24.dp)
            ) {
                when (EditorTab.entries[tab]) {
                    EditorTab.PHOTO -> PhotoTab(current, ui, importing, onPick = vm::importPhoto, onRemove = vm::removePhoto, onUpdate = vm::update)
                    EditorTab.DETAILS -> DetailsTab(current, pack, vm::update)
                    EditorTab.DESIGN -> DesignTab(current, ui, vm::update)
                    EditorTab.TEXT -> TextTab(current, pack, vm::update)
                }
            }
        }
    }
}
