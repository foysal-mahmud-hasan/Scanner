package com.foysal.wsTech.scanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.PersistableBundle
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.foysal.wsTech.scanner.databinding.ActivityMainBinding
import java.io.IOException
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.gms.vision.Detector.Detections
import java.util.jar.Manifest

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner : CodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
            PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), 1)
        }else{
            startScanning()
        }
    }

    private fun startScanning() {
        val scannerView : View? = findViewById(R.id.scanner_view)
        codeScanner = CodeScanner(this, scannerView as CodeScannerView)
        codeScanner.camera = CodeScanner.CAMERA_BACK
        codeScanner.formats = CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode = AutoFocusMode.SAFE
        codeScanner.scanMode = ScanMode.SINGLE
        codeScanner.isFlashEnabled = true
        codeScanner.isFlashEnabled = false

        codeScanner.decodeCallback = DecodeCallback {
            runOnUiThread{
                Toast.makeText(this, "Scan Result: ${it.text}", Toast.LENGTH_LONG).show()
            }
        }
        codeScanner.errorCallback = ErrorCallback {
            runOnUiThread{
                Toast.makeText(this, "Camera initialization error : ${it.message}", Toast.LENGTH_LONG).show()
            }
        }
        scannerView.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Camera Permission granted", Toast.LENGTH_LONG).show()
            }else{
                Toast.makeText(this, "Camera Permission denied", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()
    }
    /* private val requestCodeCameraPermission = 1001
     private lateinit var cameraSource: CameraSource
     private lateinit var barcodeDetector: BarcodeDetector
     private var scannedValue = ""
     private lateinit var binding: ActivityMainBinding

     override fun onCreate(savedInstanceState: Bundle?) {
         super.onCreate(savedInstanceState)
         binding = ActivityMainBinding.inflate(layoutInflater)
         val view = binding.root
         setContentView(view)


         if (ContextCompat.checkSelfPermission(
                 this@MainActivity, android.Manifest.permission.CAMERA
             ) != PackageManager.PERMISSION_GRANTED
         ) {
             askForCameraPermission()
         } else {
             setupControls()
         }

         val aniSlide: Animation =
             AnimationUtils.loadAnimation(this@MainActivity, R.anim.scanner_animation)
         binding.barcodeLine.startAnimation(aniSlide)
     }


     private fun setupControls() {
         barcodeDetector =
             BarcodeDetector.Builder(this).setBarcodeFormats(Barcode.ALL_FORMATS).build()

         cameraSource = CameraSource.Builder(this, barcodeDetector)
             .setRequestedPreviewSize(1920, 1080)
             .setAutoFocusEnabled(true) //you should add this feature
             .build()

         binding.cameraSurfaceView.getHolder().addCallback(object : SurfaceHolder.Callback {
             @SuppressLint("MissingPermission")
             override fun surfaceCreated(holder: SurfaceHolder) {
                 try {
                     //Start preview after 1s delay
                     cameraSource.start(holder)
                 } catch (e: IOException) {
                     e.printStackTrace()
                 }
             }

             @SuppressLint("MissingPermission")
             override fun surfaceChanged(
                 holder: SurfaceHolder,
                 format: Int,
                 width: Int,
                 height: Int
             ) {
                 try {
                     cameraSource.start(holder)
                 } catch (e: IOException) {
                     e.printStackTrace()
                 }
             }

             override fun surfaceDestroyed(holder: SurfaceHolder) {
                 cameraSource.stop()
             }
         })


         barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
             override fun release() {
                 Toast.makeText(applicationContext, "Scanner has been closed", Toast.LENGTH_SHORT)
                     .show()
             }

             override fun receiveDetections(detections: Detections<Barcode>) {
                 val barcodes = detections.detectedItems
                 if (barcodes.size() == 1) {
                     scannedValue = barcodes.valueAt(0).rawValue


                     //Don't forget to add this line printing value or finishing activity must run on main thread
                     runOnUiThread {
                         cameraSource.stop()
                         Toast.makeText(this@MainActivity, "value- $scannedValue", Toast.LENGTH_SHORT).show()
                         finish()
                     }
                 }else
                 {
                     Toast.makeText(this@MainActivity, "value- else", Toast.LENGTH_SHORT).show()

                 }
             }
         })
     }

     private fun askForCameraPermission() {
         ActivityCompat.requestPermissions(
             this@MainActivity,
             arrayOf(android.Manifest.permission.CAMERA),
             requestCodeCameraPermission
         )
     }

     override fun onRequestPermissionsResult(
         requestCode: Int,
         permissions: Array<out String>,
         grantResults: IntArray
     ) {
         super.onRequestPermissionsResult(requestCode, permissions, grantResults)
         if (requestCode == requestCodeCameraPermission && grantResults.isNotEmpty()) {
             if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                 setupControls()
             } else {
                 Toast.makeText(applicationContext, "Permission Denied", Toast.LENGTH_SHORT).show()
             }
         }
     }

     override fun onDestroy() {
         super.onDestroy()
         cameraSource.stop()
     }*/
}