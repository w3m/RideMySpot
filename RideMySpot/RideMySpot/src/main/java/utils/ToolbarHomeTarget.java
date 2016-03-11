package utils;

import android.graphics.Point;
import android.support.annotation.IdRes;
import android.support.v7.widget.Toolbar;

import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.Target;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

/**
 * Created by steeveguillaume on 11/03/16.
 */
public class ToolbarHomeTarget implements Target {

    private final Toolbar toolbar;
    private final int menuItemId;

    public ToolbarHomeTarget(Toolbar toolbar, @IdRes int itemId) {
        this.toolbar = toolbar;
        this.menuItemId = itemId;
    }

    @Override
    public Point getPoint() {
        ViewTarget viewTarget = new ViewTarget(toolbar.findViewById(menuItemId));
        int half = toolbar.getWidth()/2;
        int x = viewTarget.getPoint().x - ((viewTarget.getPoint().x - half) * 2);
        return new PointTarget(x, viewTarget.getPoint().y).getPoint();
    }

}
