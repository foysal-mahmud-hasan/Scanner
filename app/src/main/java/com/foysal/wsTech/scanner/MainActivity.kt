package com.foysal.wsTech.scanner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.foysal.wsTech.scanner.Common.Common
import com.foysal.wsTech.scanner.Model.APIResponse
import com.foysal.wsTech.scanner.Remote.IMyApi
import retrofit2.Call
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private lateinit var codeScanner : CodeScanner
    private lateinit var mService : IMyApi
/*    private lateinit var textTitle : TextView
    private lateinit var textMsg : TextView
    private lateinit var button: Button
    private lateinit var imageView: ImageView*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mService = Common.api
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
                registerBarcode(it.text.toString())
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
//        val aniSlide: Animation =
//            AnimationUtils.loadAnimation(this@MainActivity, R.anim.scanner_animation)
//        binding.barcodeLine.startAnimation(aniSlide)
    }

    private fun registerBarcode(barcode: String) {

        mService.registerUser(barcode)
            .enqueue(object : retrofit2.Callback<APIResponse> {
                override fun onResponse(call: Call<APIResponse>, response: Response<APIResponse>) {
                    if (response.body()!!.error)
                        showWarningAlertDialog(response)
//                        Toast.makeText(this@MainActivity, response.body()!!.error_msg,
//                            Toast.LENGTH_LONG).show()
                    else{
                        showSuccessAlertDialog(response)
//                        Toast.makeText(this@MainActivity, "Register Successful"
//                                + response.body()!!.uid, Toast.LENGTH_LONG).show()
                        }
                }

                override fun onFailure(call: Call<APIResponse>, t: Throwable) {
                    Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
                }


            })

    }

    private fun showWarningAlertDialog(response: Response<APIResponse>) {
        val builder = AlertDialog.Builder(this@MainActivity, R.style.AlertDialogTheme)
        val view = LayoutInflater.from(this@MainActivity).inflate(
            R.layout.layout_warning_dialog,
            findViewById<View>(R.id.layoutDialogContainerWarning) as? ConstraintLayout
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleWarning) as TextView).text = "Warning"
        (view.findViewById<View>(R.id.textMessageWarning) as TextView).text = "This Qr Code Already Registered For ${response.body()!!.uid} + ${response.body()!!.error_msg}"
        (view.findViewById<View>(R.id.buttonActionWarning) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconWarning) as ImageView).setImageResource(R.drawable.warning)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionWarning).setOnClickListener {
            codeScanner.startPreview()
//            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()
    }

    private fun showSuccessAlertDialog(response: Response<APIResponse>) {
        val builder = AlertDialog.Builder(this@MainActivity, R.style.AlertDialogTheme)
        val view = LayoutInflater.from(this@MainActivity).inflate(
            R.layout.layout_success_dialog,
            findViewById<View>(R.id.layoutDialogContainerSuccess) as? ConstraintLayout
        )
        builder.setView(view)
        (view.findViewById<View>(R.id.textTitleSuccess) as TextView).text = "Success"
        (view.findViewById<View>(R.id.textMessageSuccess) as TextView).text = "This Qr Code Successfully Registered For ${response.body()!!.uid}"
        (view.findViewById<View>(R.id.buttonActionSuccess) as Button).text = "Scan Again"
        (view.findViewById<View>(R.id.imageIconSuccess) as ImageView).setImageResource(R.drawable.done)
        val alertDialog = builder.create()
        view.findViewById<View>(R.id.buttonActionSuccess).setOnClickListener {
            codeScanner.startPreview()
//            Toast.makeText(this@MainActivity, "Success", Toast.LENGTH_LONG).show()
        }
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0x0))
        }
        alertDialog.show()
    }



/*    java code
    private void showSuccessDialog()
    {

        AlertDialog.Builder builder = new AlertDialog.Builder(
            MainActivity.this,
            R.style.AlertDialogTheme
        );
        View view = LayoutInflater . from (MainActivity.this).inflate(
            R.layout.layout_success_dialog,
            (ConstraintLayout) findViewById (R.id.layoutDialogContainerSuccess)
        );
        builder.setView(view);
        ((TextView)) view . findViewById (R.id.textTitleSuccess).setText("SDFS");
        ((TextView)) view . findViewById (R.id.textMessageSuccess).setText("SDFS");
        ((Button)) view . findViewById (R.id.buttonActionSuccess).setText("SDFS");
        ((ImageView)) view . findViewById (R.id.imageIconSuccess).setImageResource(R.drawable.done);

        final AlertDialog = builder . create ();

        alertDialog.show();

    }*/

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
            codeScanner.startPreview()
        }
    }

    override fun onPause() {
        if(::codeScanner.isInitialized){
            codeScanner.releaseResources()
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