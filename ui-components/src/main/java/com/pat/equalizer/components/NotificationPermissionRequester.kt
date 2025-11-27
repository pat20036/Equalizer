import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat
import com.pat.equalizer.components.R

@Composable
fun NotificationPermissionRequester(
    shouldShowRationale: () -> Boolean,
    onPermissionGranted: () -> Unit
) {
    val context = LocalContext.current
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return

    var showRationale by remember { mutableStateOf(false) }
    var showSettingsDialog by remember { mutableStateOf(false) }

    val settingsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            onPermissionGranted()
            showSettingsDialog = false
            showRationale = false
        } else {
            showRationale = shouldShowRationale()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) {
            showRationale = shouldShowRationale()
            if (!showRationale) showSettingsDialog = true
        } else onPermissionGranted()
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    if (showRationale) {
        AlertDialog(
            onDismissRequest = { showRationale = false },
            title = { Text(stringResource(id = R.string.notification_permission_rationale_dialog_title)) },
            text = { Text(stringResource(id = R.string.notification_permission_rationale_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showRationale = false
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }) { Text(stringResource(id = R.string.notification_permission_rationale_dialog_positive_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showRationale = false }) { Text(stringResource(id = R.string.notification_permission_rationale_dialog_negative_button)) }
            }
        )
    }

    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = { Text(stringResource(id = R.string.notification_permission_dismissed_dialog_title)) },
            text = { Text(stringResource(id = R.string.notification_permission_dismissed_dialog_message)) },
            confirmButton = {
                TextButton(onClick = {
                    showSettingsDialog = false
                    val intent = Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", context.packageName, null)
                    )
                    settingsLauncher.launch(intent)
                }) { Text(stringResource(id = R.string.notification_permission_dismissed_dialog_positive_button)) }
            },
            dismissButton = {
                TextButton(onClick = { showSettingsDialog = false }) { Text(stringResource(id = R.string.notification_permission_rationale_dialog_negative_button)) }
            }
        )
    }
}