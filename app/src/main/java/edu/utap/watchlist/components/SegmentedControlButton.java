package edu.utap.watchlist.components;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatRadioButton;
import edu.utap.watchlist.R;

/**
 * 
 */
public class SegmentedControlButton
    extends AppCompatRadioButton {
    /**
     * 
     */
    public SegmentedControlButton( Context context ) {
        super( context );
    }

    /**
     * 
     */
    public SegmentedControlButton( Context context, AttributeSet attrs ) {
        // It would be great to simply pass in R.style.SegmentedControlButton here, but that won't work due to:
        // https://code.google.com/p/android/issues/detail?id=12683
        super( context, attrs, R.attr.segmentedControlButtonStyle );
    }

    /**
     * 
     */
    public SegmentedControlButton( Context context, AttributeSet attrs, int defStyle ) {
        super( context, attrs, defStyle );
    }
}
