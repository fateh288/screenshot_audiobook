package com.example.screenshotaudiobook

import android.graphics.Bitmap
import android.graphics.Rect
import android.util.SparseArray
import com.google.android.gms.vision.Frame
import com.google.android.gms.vision.text.TextBlock
import com.google.android.gms.vision.text.TextRecognizer

class TextDetector(
    val textRecognizer: TextRecognizer?,
    val bitmap: Bitmap?
){
    private var textBlockSparseArray: SparseArray<TextBlock>? = SparseArray()

    init {
        textBlockSparseArray = textRecognizer?.detect(Frame.Builder().setBitmap(bitmap).build())
    }
    fun getTextBoxes() : List<Rect> {
        val size = textBlockSparseArray?.size()
        val textBoxList = MutableList(size!!) { Rect() }
        for (textBlock : TextBlock in textBlockSparseArray){
            textBoxList.add(textBlock.boundingBox)
        }
        return textBoxList
    }
    fun getTextList() : List<String> {
        val size = textBlockSparseArray?.size()
        val textList = MutableList(0) { String() }
        for (textBlock : TextBlock in textBlockSparseArray){
            textList.add(textBlock.value)
        }
        return textList
    }
}

private operator fun <E> SparseArray<E>?.iterator(): Iterator<E> {
    val nSize = this!!.size()
    return object : Iterator<E> {
        var i = 0
        override fun hasNext(): Boolean = i < nSize
        override fun next(): E = get(i++)
    }
}
