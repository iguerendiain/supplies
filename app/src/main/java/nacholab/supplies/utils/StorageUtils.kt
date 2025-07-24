package nacholab.supplies.utils

import android.app.Application
import android.net.Uri
import java.io.File

fun Application.copyMediaStoreUriToFile(uri: Uri, internalTarget: File){
    contentResolver.openInputStream(uri).use { input ->
        internalTarget.outputStream().use { output ->
            input?.copyTo(output)
        }
    }
}
