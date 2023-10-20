package com.example.face_detection_app

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.params.Face
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions

class MainActivity() : AppCompatActivity(), Parcelable {
    constructor(parcel: Parcel) : this() {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MainActivity> {
        override fun createFromParcel(parcel: Parcel): MainActivity {
            return MainActivity(parcel)
        }

        override fun newArray(size: Int): Array<MainActivity?> {
            return arrayOfNulls(size)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var ButtonCamera = findViewById<Button>(R.id.face_btn)
        ButtonCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            if (intent.resolveActivity(packageManager) != null) {
                startActivityForResult(intent, 123)
            } else {
                Toast.makeText(this, "oops kuch galat hua h", Toast.LENGTH_SHORT).show()

            }
        }
        @SuppressLint("SuspiciousIndentation")
        fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (requestCode == 123 && resultCode == RESULT_OK) {
           val extras=data?.extras
            val bitmap=extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    detectFace(bitmap)
                }
            }

        }
    }  private fun detectFace(bitmap: Bitmap){
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode (FaceDetectorOptions. PERFORMANCE_MODE_ACCURATE)
            .setLandmarkMode (FaceDetectorOptions. LANDMARK_MODE_ALL)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
            .build()

        val detector=FaceDetection.getClient(options)

        val image = InputImage.fromBitmap (bitmap, 0)

        val result = detector.process(image)
            .addOnSuccessListener { faces ->
                // Task completed successfully , our face is successfully detected
                var resulText=""
                var i=1
                for(face in faces){
                    resulText="Face Number : $i" +
                            "\n smile : ${face.smilingProbability?.times(100)}%" +
                            "\nLeft Eye Open :${face.leftEyeOpenProbability?.times(100)}%" +
                            "\nRight Eye Open :${face.rightEyeOpenProbability?.times(100)}%"
                    i++
                    if(faces.isEmpty()){
                        Toast.makeText(this, "NO FACE DETECTED", Toast.LENGTH_SHORT).show()
                    } else{
                        Toast.makeText(this, resulText, Toast.LENGTH_LONG).show()
                    }
                }

            }
            .addOnFailureListener { e ->
                // Task failed with an exception, maaf karna bhaisahab aapki sakal gandi h
                // ...
                Toast.makeText(this, "Something went wrong", Toast.LENGTH_SHORT).show()
            }

    }
}

