package utils;

import android.graphics.Point;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;

import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by steeveguillaume on 09/03/16.
 */

public class ToolbarActionItemTarget implements Target {

    private final Toolbar toolbar;
    private final int menuItemId;

    public ToolbarActionItemTarget(Toolbar toolbar, @IdRes int itemId) {
        this.toolbar = toolbar;
        this.menuItemId = itemId;
    }

    @Override
    public Point getPoint() {
        return new ViewTarget(toolbar.findViewById(menuItemId)).getPoint();
    }

}
