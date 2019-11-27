package co.vieln.mlkitexperiment.Libs.barcode_recognition;

import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;

import java.io.IOException;
import java.util.List;

import co.vieln.mlkitexperiment.Libs.CameraImageGraphic;
import co.vieln.mlkitexperiment.Libs.FrameMetadata;
import co.vieln.mlkitexperiment.Libs.GraphicOverlay;
import co.vieln.mlkitexperiment.Libs.VisionProcessorBase;
import co.vieln.mlkitexperiment.R;

public class BarcodeScanningProcessor extends VisionProcessorBase<List<FirebaseVisionBarcode>> {

    private static final String TAG = "BarcodeScanProc";

    private final FirebaseVisionBarcodeDetector detector;

    private List<FirebaseVisionBarcode> barcodes;

    public BarcodeScanningProcessor() {
        // Note that if you know which format of barcode your app is dealing with, detection will be
        // faster to specify the supported barcode formats one by one, e.g.
        // new FirebaseVisionBarcodeDetectorOptions.Builder()
        //     .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
        //     .build();
        detector = FirebaseVision.getInstance().getVisionBarcodeDetector();
    }

    @Override
    public void process(Bitmap bitmap, GraphicOverlay graphicOverlay) {

    }

    @Override
    public void stop() {
        try {
            detector.close();
        } catch (IOException e) {
            Log.e(TAG, "Exception thrown while trying to close Barcode Detector: " + e);
        }
    }

    @Override
    protected Task<List<FirebaseVisionBarcode>> detectInImage(FirebaseVisionImage image) {
        return detector.detectInImage(image);
    }

    @Override
    protected void onSuccess(@NonNull List<FirebaseVisionBarcode> results, @NonNull FrameMetadata frameMetadata, @NonNull GraphicOverlay graphicOverlay) {
        graphicOverlay.clear();

        barcodes = results;
        for (int i = 0; i < barcodes.size(); ++i) {
            FirebaseVisionBarcode barcode = barcodes.get(i);
            BarcodeGraphic barcodeGraphic = new BarcodeGraphic(graphicOverlay, barcode);
            graphicOverlay.add(barcodeGraphic);
        }
        graphicOverlay.postInvalidate();
    }

    @Override
    protected void onFailure(@NonNull Exception e) {
        Log.e(TAG, "Barcode detection failed " + e);
    }

    public List<FirebaseVisionBarcode> getBarcodes(){
        return barcodes;
    }

}
