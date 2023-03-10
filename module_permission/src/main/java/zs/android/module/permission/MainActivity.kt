package zs.android.module.permission

import android.Manifest
import android.content.ContentValues
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.FileProvider
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import java.io.File
import java.io.FileInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var startActivityForResult: ActivityResultLauncher<Intent>
    private lateinit var startIntentSenderForResult: ActivityResultLauncher<IntentSenderRequest>
    private lateinit var requestPermission: ActivityResultLauncher<String>
    private lateinit var requestMultiplePermissions: ActivityResultLauncher<Array<String>>
    private lateinit var openDocument: ActivityResultLauncher<Array<String>>
    private lateinit var openMultipleDocuments: ActivityResultLauncher<Array<String>>
    private lateinit var openDocumentTree: ActivityResultLauncher<Uri?>
    private lateinit var takePicturePreview: ActivityResultLauncher<Void?>
    private lateinit var takePicture: ActivityResultLauncher<Uri>
    private lateinit var captureVideo: ActivityResultLauncher<Uri>
    private lateinit var pickContact: ActivityResultLauncher<Void?>
    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var createDocument: ActivityResultLauncher<String>
    private lateinit var getMultipleContents: ActivityResultLauncher<String>

    private lateinit var mImageView: AppCompatImageView

    var uri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setPictureClick()
        setCallback()

        mImageView = findViewById(R.id.acIv)
    }

    private fun setCallback() {
        //??????????????????????????????????????????setResult(RESULT_OK)????????????????????????
        startActivityForResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.i("print_logs", "startActivityForResult ???????????????")
                } else {
                    Log.e("print_logs", "startActivityForResult ???????????????")
                }
            }
        findViewById<AppCompatButton>(R.id.onStartActivityForResult).setOnClickListener {
            startActivityForResult.launch(
                Intent(this, SubActivity::class.java)
            )
        }


        startIntentSenderForResult =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
                if (it.resultCode == RESULT_OK) {
                    Log.i("print_logs", "startIntentSenderForResult")
                }
            }
        findViewById<AppCompatButton>(R.id.onStartIntentSenderForResult).setOnClickListener {
//            startIntentSenderForResult.launch(IntentSenderRequest.Builder().build())
        }


        //????????????????????????
        requestPermission = registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            if (it) {
                Log.i("print_logs", "requestPermission ?????????WRITE_EXTERNAL_STORAGE")

            } else {
                Log.e("print_logs", "requestPermission ?????????WRITE_EXTERNAL_STORAGE")

            }
        }
        findViewById<AppCompatButton>(R.id.onRequestPermission).setOnClickListener {
            requestPermission.launch(
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }

        //????????????????????????
        requestMultiplePermissions =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (it[Manifest.permission.READ_EXTERNAL_STORAGE]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions ?????????READ_EXTERNAL_STORAGE"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions ?????????READ_EXTERNAL_STORAGE"
                    )
                }

                if (it[Manifest.permission.CAMERA]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions ?????????CAMERA"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions ?????????CAMERA"
                    )
                }

                if (it[Manifest.permission.READ_CONTACTS]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions ?????????READ_CONTACTS"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions ?????????READ_CONTACTS"
                    )
                }

                if (it[Manifest.permission.WRITE_CONTACTS]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions ?????????WRITE_CONTACTS"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions ?????????WRITE_CONTACTS"
                    )
                }

                if (it[Manifest.permission.RECORD_AUDIO]!!) {
                    Log.i(
                        "print_logs", "requestMultiplePermissions ?????????RECORD_AUDIO"
                    )

                } else {
                    Log.e(
                        "print_logs", "requestMultiplePermissions ?????????RECORD_AUDIO"
                    )
                }
            }

        findViewById<AppCompatButton>(R.id.onRequestMultiplePermissions).setOnClickListener {
            requestMultiplePermissions.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }

        //?????????????????????????????????????????????(file:/http:/content:)?????????Uri???
        createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "createDocument: ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onCreateDocument).setOnClickListener {
            createDocument.launch(
                null
            )
        }

        //????????????????????????????????????????????????????????????????????????Uri
        openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                val path = Uri2PathUtil.getRealPathFromUri(this, it)
                Log.i("print_logs", "openDocument $path")
            }
        }
        findViewById<AppCompatButton>(R.id.onOpenDocument).setOnClickListener {
            openDocument.launch(
                arrayOf("image/*", "text/plain")
            )
        }


        //????????????????????????????????????????????????????????????????????????Uri??????List????????????
        openMultipleDocuments =
            registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) {
                it.forEach { uri ->
                    if (uri != null) {
                        Log.i("print_logs", "openMultipleDocuments $uri")
                    }
                }
            }
        findViewById<AppCompatButton>(R.id.onOpenMultipleDocuments).setOnClickListener {
            openMultipleDocuments.launch(
                arrayOf("image/*", "text/plain")
            )
        }


        //?????????????????????????????????????????????????????????????????????Uri??????????????????????????????????????????????????????????????????
        openDocumentTree = registerForActivityResult(ActivityResultContracts.OpenDocumentTree()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "openDocumentTree ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onOpenDocumentTree).setOnClickListener {
            openDocumentTree.launch(
                null
            )
        }


        //??????MediaStore.ACTION_IMAGE_CAPTURE?????????????????????Bitmap??????
        takePicturePreview =
            registerForActivityResult(ActivityResultContracts.TakePicturePreview()) {
                Log.i("print_logs", "takePicturePreview: $it")

                with(mImageView) {
                    visibility = View.VISIBLE
                    setImageBitmap(it)
                }
            }
        findViewById<AppCompatButton>(R.id.onTakePicturePreview).setOnClickListener {
            takePicturePreview.launch(null)
        }


        val cameraPath: String =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).absolutePath + File.separator + "Camera"
        //??????MediaStore.ACTION_IMAGE_CAPTURE???????????????????????????????????????Uri???????????????true??????????????????
        takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) {
            Log.i("print_logs", "??????:: $it ,uri=$uri")
            if (it) {
                with(mImageView) {
                    uri?.let { _path ->
                        val name = _path.toString().substringAfterLast(File.separator)
                        Log.i("print_logs", "????????????: ${cameraPath}/$name")
                        visibility = View.VISIBLE
                        setImageURI(uri)
                    }
                }
            }
        }

        Log.i("print_logs", this.applicationContext.packageName)


        findViewById<AppCompatButton>(R.id.onTakePicture).setOnClickListener {
//            uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                val values = ContentValues().apply {
//                    put(
//                        MediaStore.MediaColumns.DISPLAY_NAME,
//                        "IMG_${System.currentTimeMillis()}.jpg"
//                    )
//                    put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DCIM}/Camera")
//                }
//                contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
//            } else {
//              null
//            }

            val file = File(cameraPath).apply {
                if (!this.exists()) {
                    this.mkdirs()
                }else{
                    if (this.isDirectory) {
                        this.listFiles()?.forEach { file ->
                            if (file.isFile) {
                                file.delete()
                            }
                        }
                    }
                }
            }

            val photoFile = File.createTempFile("IMG_", ".jpg", file)
            uri = FileProvider.getUriForFile(
                this,
                "${BuildConfig.APPLICATION_ID}.providers",
                photoFile
            )

//            cameraPath?.let { folder ->
//                if (folder.exists() && folder.isDirectory) {
//                    Log.i("print_logs", "MainActivity::setCallback: ?????????????????????")
//                    folder.listFiles().forEach { file->
//                        Log.i("print_logs", "??????:: ${file.name}")
//                        if (file.isFile) {
//                            Log.i("print_logs", "??????????????? ${file.name}")
//                            file.delete()
//                        }
//                    }
//                }
//            }

            takePicture.launch(uri)
        }


        //??????MediaStore.ACTION_VIDEO_CAPTURE ?????????????????????????????????Uri??????????????????????????????
        captureVideo = registerForActivityResult(ActivityResultContracts.CaptureVideo()) {
            Log.i("print_logs", "takeVideo: $it")
        }
        findViewById<AppCompatButton>(R.id.onCaptureVideo).setOnClickListener {
            val uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(
                        MediaStore.MediaColumns.DISPLAY_NAME,
                        "${System.currentTimeMillis()}_test.mp4"
                    )
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_MOVIES)
                    put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
                }
                contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)
            } else {
                null
            }
            captureVideo.launch(
                uri
            )
        }


        //????????????APP???????????????
        pickContact = registerForActivityResult(ActivityResultContracts.PickContact()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "pickContact: $it")
                try {
                    val cursor = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
                    )
                    if (cursor != null) {  //??????????????????
                        while (cursor.moveToNext()) {
                            val name =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                            val number =
                                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                            Log.i("print_logs", "MainActivity::onCreate: $name, $number")
                        }
                    }
                    cursor?.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        findViewById<AppCompatButton>(R.id.onPickContact).setOnClickListener {
            pickContact.launch(null)
        }

        //????????????????????????????????????????????????ContentResolver#openInputStream(Uri)?????????????????????Uri?????????content://????????????
        //??????????????????????????????Intent#CATEGORY_OPENABLE, ?????????????????????????????????
        getContent = registerForActivityResult(ActivityResultContracts.GetContent()) {
            if (it != null && !TextUtils.isEmpty(it.path)) {
                Log.i("print_logs", "getContent: ${it.path}")
            }
        }
        findViewById<AppCompatButton>(R.id.onGetContent).setOnClickListener {
            getContent.launch("text/plain")
        }


        getMultipleContents =
            registerForActivityResult(ActivityResultContracts.GetMultipleContents()) {
                if (it.isNotEmpty()) {
                    it.forEach { uri ->
                        if (it != null && !TextUtils.isEmpty(uri.path)) {
                            Log.i("print_logs", "getMultipleContents: ${uri.path}")
                        }
                    }
                }
            }
        findViewById<AppCompatButton>(R.id.onGetMultipleContents).setOnClickListener {
            getMultipleContents.launch(
                "text/plain"
            )
        }
    }


    private fun setPictureClick() {

        findViewById<AppCompatButton>(R.id.acBtn_getImage).setOnClickListener {
            getImage(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getSystemPicture).setOnClickListener {
            getSystemPicture(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getCamera).setOnClickListener {
            getCamera(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getAlbum).setOnClickListener {
            getAlbum(it)
        }

        findViewById<AppCompatButton>(R.id.acBtn_getAllAlbum).setOnClickListener {
            getAllAlbum(it)
        }
    }

    //????????????
    private fun getImage(view: View) {
        PictureSelector.create(this)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //??????????????????
    private fun getSystemPicture(view: View) {
        PictureSelector.create(this)
            .openSystemGallery(SelectMimeType.ofImage())
            .forSystemResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //??????
    private fun getCamera(view: View) {
        PictureSelector.create(this)
            .openCamera(SelectMimeType.ofImage())
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    Log.i("print_logs", "MainActivity::onResult: ${result?.get(0)?.realPath}")
                    with(mImageView) {
                        visibility = View.VISIBLE
                        setImageURI(Uri.parse(result?.get(0)?.realPath))
                    }
                }

                override fun onCancel() {
                    Log.e("print_logs", "MainActivity::onCancel: ")
                }
            })
    }

    //????????????
    private fun getAlbum(view: View) {
        PictureSelector.create(this)
            .dataSource(SelectMimeType.ofImage())
            .obtainAlbumData { result ->
                result.forEach {

                    Log.i("print_logs", "????????????${it?.firstImagePath}, ${it.data.size}")
                    it.data.forEach { file ->
                        Log.i("print_logs", "????????????: $file")
                    }
                }
            }
    }

    //????????????
    private fun getAllAlbum(view: View) {
        PictureSelector.create(this)
            .dataSource(SelectMimeType.ofAll())
            .obtainMediaData { result ->
                result.forEach {
                    Log.i("print_logs", "??????: ${it?.realPath}")
                }
            }
    }

    fun getAllDataFileName(folderPath: String?): ArrayList<String>? {
        val fileList = ArrayList<String>()
        val file = File(folderPath)
        val tempList = file.listFiles()
        for (i in tempList.indices) {
            if (tempList[i].isFile) {
                Log.w("print_logs", "??????:: ${tempList[i].name}")
                val fileName = tempList[i].name
                if (fileName.endsWith(".jpg") or fileName.endsWith(".png")) {    //  ???????????????????????????????????????
                    fileList.add(fileName)
                }
            }
        }
        return fileList
    }
}