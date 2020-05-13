package com.example.screenshotaudiobook

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.text.TextRecognizer
import java.lang.Exception
import java.util.*
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private var bitmap: Bitmap? = null
    private var imageView: ImageView? = null
    private var tts: TextToSpeech? = null
    private var textDetector: TextDetector? = null
    private var GALLERY_PERMISSION = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (ContextCompat.checkSelfPermission(applicationContext,
                "android.permission.READ_EXTERNAL_STORAGE")!=PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),GALLERY_PERMISSION)
        }
        setContentView(R.layout.activity_main)
        val drawBoxesButton = findViewById<Button>(R.id.button_show_boxes);
        drawBoxesButton.setOnClickListener{
            drawTextBoxes();
        }
        val text2SpeechButton = findViewById<Button>(R.id.button_text_to_speech);
        text2SpeechButton.setOnClickListener {
            playAudio()
        }
        val imageChooserButton = findViewById<Button>(R.id.image_chooser_button)
        imageView = findViewById<ImageView>(R.id.image_view_screenshot)
        imageChooserButton.setOnClickListener {
            selectImage();
        }
        val textButton = findViewById<Button>(R.id.button_get_text);
        val textView = findViewById<TextView>(R.id.text_view_bb_text);
        textButton.setOnClickListener {
            val textList = getText()
            textView.text = textList.joinToString(separator = "\n")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            val selectedImage = data?.data
            val filePathColumn = arrayOf<String>(MediaStore.Images.Media.DATA)
            val cursor = contentResolver.query(selectedImage!!, filePathColumn,
                null, null, null)
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePathColumn[0])
            val picturePath = columnIndex?.let { cursor.getString(it) }
            var options = BitmapFactory.Options()
            options.inMutable = true
            bitmap = BitmapFactory.decodeFile(picturePath, options)
            imageView?.setImageBitmap(bitmap)
            textDetector = TextDetector(TextRecognizer.Builder(applicationContext).build(), bitmap)
            cursor?.close()
        }
    }

    private fun selectImage() {
        val pickPhoto = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(pickPhoto, 1);
    }

    private fun getText(): List<String> {
        val text = textDetector?.getTextList() ?: emptyList<String>()
        if (text.isEmpty() || textDetector == null) {
            Toast.makeText(
                applicationContext,
                "No text detected or Image not selected",
                Toast.LENGTH_SHORT
            ).show()
            //throw Exception("invalid image...image expected")
        }
        return text
    }

    private fun playAudio() {
        val textList = getText()
        playAudio(textList,tts)
    }
    private fun drawTextBoxes(){
        var textBoxes = getTextBoxes()
        drawRects(bitmap,textBoxes)
    }
    private fun getTextBoxes() : List<Rect> {
        val textRects = textDetector?.getTextBoxes() ?: emptyList<Rect>()
        if (textRects.isEmpty() || textDetector==null){
            Toast.makeText(
                applicationContext,
                "No text detected or Image not selected",
                Toast.LENGTH_SHORT
            ).show()
        }
        return textRects
    }

    override fun onResume() {
        super.onResume()
        if (tts == null) {
            tts = TextToSpeech(this, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    Log.d("OnInitListener", "Text to speech engine started successfully.")
                    tts?.language = Locale.US
                    //var status = tts?.speak("Hello World", TextToSpeech.QUEUE_ADD, null, "trial")
                } else {
                    Log.d("OnInitListener", "Error starting the text to speech engine.")
                }
            })
        }
    }

    override fun onStop() {
        super.onStop()
        tts?.stop()
    }
    override fun onDestroy() {
        super.onDestroy()
        tts?.shutdown()
        tts = null
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            GALLERY_PERMISSION ->{
                if ((grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED)) {
                    finish()
                }
            }
        }
    }
}
