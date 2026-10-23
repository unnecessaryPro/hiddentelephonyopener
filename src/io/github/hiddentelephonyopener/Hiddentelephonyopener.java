package io.github.hiddentelephonyopener;

import com.google.appinventor.components.annotations.Extension;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.errors.YailRuntimeError;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;

@Extension(
        description = "Extension component for Hiddentelephonyopener. Built with <3 and Rush.",
        icon = "icon.png"
)
public class Hiddentelephonyopener extends AndroidNonvisibleComponent {

    public Hiddentelephonyopener(ComponentContainer container) {
        super(container.$form());
    }

    @SimpleFunction(description = "Returns the sum of the given list of integers.")
    public int SumAll(YailList listOfInts) {
        int sum = 0;

        for (final Object o : listOfInts.toArray()) {
            try {
                sum += Integer.parseInt(o.toString());
            } catch (NumberFormatException e) {
                throw new YailRuntimeError(e.toString(), "NumberFormatException");
            }
        }

        return sum;
    }
}
