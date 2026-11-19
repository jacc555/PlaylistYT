package pl.kolejkapiosenek.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import pl.kolejkapiosenek.R
import pl.kolejkapiosenek.data.Song
import pl.kolejkapiosenek.data.VideoLinks

@Composable
fun QueueScreen(vm: QueueViewModel = viewModel()) {
    val context = LocalContext.current
    var links by rememberSaveable { mutableStateOf("") }
    val songs = vm.songs
    val muted = MaterialTheme.colorScheme.onSurfaceVariant

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding(),
            contentAlignment = Alignment.TopCenter,
        ) {
            LazyColumn(
                modifier = Modifier
                    .widthIn(max = 560.dp)
                    .fillMaxWidth()
                    .fillMaxHeight(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                item(key = "header") {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Kolejka piosenek",
                            fontSize = 34.sp,
                            lineHeight = 38.sp,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "Jeden przycisk włącza wszystkie piosenki z listy po kolei. " +
                                "Kiedy jedna się skończy, YouTube sam puszcza następną.",
                            color = muted,
                            fontSize = 17.sp,
                        )
                    }
                }

                item(key = "play") {
                    PlayButton(
                        enabled = songs.isNotEmpty(),
                        onClick = { openUrl(context, vm.queueUrl) },
                    )
                }

                item(key = "addTitle") { SectionTitle("Dodaj piosenki") }
                item(key = "addForm") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        OutlinedTextField(
                            value = links,
                            onValueChange = { links = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            shape = RoundedCornerShape(12.dp),
                            placeholder = {
                                Text("Wklej tu jeden lub kilka linków z YouTube, każdy w nowej linii")
                            },
                            colors = fieldColors(),
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Button(
                                onClick = { if (vm.addLinks(links)) links = "" },
                                shape = CircleShape,
                                contentPadding = PaddingValues(horizontal = 18.dp, vertical = 8.dp),
                            ) {
                                Text("Dodaj do kolejki", fontWeight = FontWeight.Bold)
                            }
                            Text(
                                text = vm.addMessage,
                                color = muted,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                    }
                }

                item(key = "listTitle") {
                    val count = if (songs.isNotEmpty()) " (${songs.size})" else ""
                    SectionTitle("Kolejność odtwarzania$count")
                }
                item(key = "shuffle") {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        PillButton(text = "Wymieszaj kolejność", onClick = { vm.shuffle() })
                        Text(
                            text = vm.shuffleMessage,
                            color = muted,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }

                if (songs.isEmpty()) {
                    item(key = "empty") {
                        Text(
                            text = "Kolejka jest pusta. Wklej link powyżej i dodaj piosenkę.",
                            color = muted,
                        )
                    }
                }

                itemsIndexed(songs, key = { _, song -> song.id }) { index, song ->
                    SongCard(
                        index = index,
                        count = songs.size,
                        song = song,
                        onRename = { vm.rename(song.id, it) },
                        onUp = { vm.move(index, -1) },
                        onDown = { vm.move(index, 1) },
                        onRemove = { vm.remove(song.id) },
                        onOpen = { openUrl(context, VideoLinks.watchUrl(song.id)) },
                    )
                }

                item(key = "tools") {
                    Column(
                        modifier = Modifier.padding(top = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            PillButton(
                                text = "Kopiuj link do kolejki",
                                onClick = {
                                    if (songs.isEmpty()) {
                                        vm.showStatus("Kolejka jest pusta.")
                                    } else {
                                        copyToClipboard(context, vm.queueUrl)
                                        vm.showStatus("Skopiowano.")
                                    }
                                },
                            )
                            Text(
                                text = vm.status,
                                color = muted,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f),
                            )
                        }
                        Text(
                            text = "Przycisk „Odtwarzaj wszystkie” otwiera kolejkę w YouTube " +
                                "(w aplikacji albo w przeglądarce). Aplikacja nie pobiera tytułów " +
                                "z YouTube, więc nazwy wpisujesz w polach na liście. Lista i nazwy " +
                                "zapisują się w tym telefonie i nie przenoszą się na inne urządzenia. " +
                                "Kolejka jest tymczasowa, a YouTube przyjmuje w niej do " +
                                "${VideoLinks.MAX_SONGS} filmów.",
                            color = muted,
                            fontSize = 14.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
)

@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text.uppercase(),
        modifier = Modifier.padding(top = 18.dp),
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        letterSpacing = 0.6.sp,
    )
}

@Composable
private fun PlayButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 84.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = PlayYellow,
            contentColor = PlayText,
            disabledContainerColor = PlayYellow.copy(alpha = 0.45f),
            disabledContentColor = PlayText.copy(alpha = 0.6f),
        ),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_play),
            contentDescription = null,
            modifier = Modifier.size(28.dp),
        )
        Spacer(Modifier.width(14.dp))
        Text(
            text = "Odtwarzaj wszystkie",
            fontSize = 21.sp,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun PillButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    compact: Boolean = false,
    description: String? = null,
) {
    val primary = MaterialTheme.colorScheme.primary
    var buttonModifier = modifier
    if (compact) buttonModifier = buttonModifier.height(36.dp)
    if (description != null) {
        buttonModifier = buttonModifier.semantics { contentDescription = description }
    }
    OutlinedButton(
        onClick = onClick,
        modifier = buttonModifier,
        enabled = enabled,
        shape = CircleShape,
        border = BorderStroke(
            width = if (compact) 1.5.dp else 2.dp,
            color = if (enabled) primary else primary.copy(alpha = 0.35f),
        ),
        contentPadding = PaddingValues(
            horizontal = if (compact) 14.dp else 18.dp,
            vertical = if (compact) 0.dp else 8.dp,
        ),
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold,
            fontSize = if (compact) 15.sp else 16.sp,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SongCard(
    index: Int,
    count: Int,
    song: Song,
    onRename: (String) -> Unit,
    onUp: () -> Unit,
    onDown: () -> Unit,
    onRemove: () -> Unit,
    onOpen: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = (index + 1).toString(),
                modifier = Modifier
                    .width(34.dp)
                    .padding(top = 10.dp),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold,
            )
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = song.name,
                    onValueChange = onRename,
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    placeholder = { Text("Wpisz tytuł z YouTube") },
                    colors = fieldColors(),
                )
                Text(
                    text = song.id,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 13.sp,
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    PillButton("↑", onUp, enabled = index > 0, compact = true, description = "Przesuń w górę")
                    PillButton("↓", onDown, enabled = index < count - 1, compact = true, description = "Przesuń w dół")
                    PillButton("Usuń", onRemove, compact = true, description = "Usuń z kolejki")
                    TextButton(onClick = onOpen) {
                        Text("Otwórz ↗", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun openUrl(context: Context, url: String) {
    try {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    } catch (e: ActivityNotFoundException) {
        Toast.makeText(context, "Nie znaleziono aplikacji do otwarcia linku.", Toast.LENGTH_SHORT).show()
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(ClipboardManager::class.java)
    clipboard?.setPrimaryClip(ClipData.newPlainText("Kolejka piosenek", text))
}
