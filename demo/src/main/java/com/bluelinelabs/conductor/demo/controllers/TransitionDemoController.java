package com.bluelinelabs.conductor.demo.controllers;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.ColorRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bluelinelabs.conductor.Controller;
import com.bluelinelabs.conductor.ControllerChangeHandler;
import com.bluelinelabs.conductor.RouterTransaction;
import com.bluelinelabs.conductor.changehandler.FadeChangeHandler;
import com.bluelinelabs.conductor.changehandler.HorizontalChangeHandler;
import com.bluelinelabs.conductor.changehandler.VerticalChangeHandler;
import com.bluelinelabs.conductor.demo.R;
import com.bluelinelabs.conductor.demo.changehandler.ArcFadeMoveChangeHandlerCompat;
import com.bluelinelabs.conductor.demo.changehandler.CircularRevealChangeHandlerCompat;
import com.bluelinelabs.conductor.demo.changehandler.FlipChangeHandler;
import com.bluelinelabs.conductor.demo.controllers.base.BaseController;
import com.bluelinelabs.conductor.demo.util.BundleBuilder;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TransitionDemoController extends BaseController {

    private static final String KEY_INDEX = "TransitionDemoController.index";

    public enum TransitionDemo {
        VERTICAL("Vertical Slide Animation", R.layout.controller_transition_demo, R.color.blue_grey_300),
        CIRCULAR("Circular Reveal Animation (on Lollipop and above, else Fade)", R.layout.controller_transition_demo, R.color.red_300),
        FADE("Fade Animation", R.layout.controller_transition_demo, R.color.blue_300),
        FLIP("Flip Animation", R.layout.controller_transition_demo, R.color.deep_orange_300),
        HORIZONTAL("Horizontal Slide Animation", R.layout.controller_transition_demo, R.color.green_300),
        ARC_FADE("Arc/Fade Shared Element Transition (on Lollipop and above, else Fade)", R.layout.controller_transition_demo_shared, 0),
        ARC_FADE_RESET("Arc/Fade Shared Element Transition (on Lollipop and above, else Fade)", R.layout.controller_transition_demo, R.color.pink_300);

        String title;
        int layoutId;
        int colorId;
        TransitionDemo(String title, @LayoutRes int layoutId, @ColorRes int colorId) {
            this.title = title;
            this.layoutId = layoutId;
            this.colorId = colorId;
        }

        public static TransitionDemo fromIndex(int index) {
            return TransitionDemo.values()[index];
        }
    }

    @Bind(R.id.tv_title) TextView mTvTitle;
    @Bind(R.id.btn_next) FloatingActionButton mBtnNext;
    @Bind(R.id.transition_root) View mContainerView;

    private TransitionDemo mTransitionDemo;

    public TransitionDemoController(int index) {
        this(new BundleBuilder(new Bundle())
                .putInt(KEY_INDEX, index)
                .build());
    }

    public TransitionDemoController(Bundle args) {
        super(args);
        mTransitionDemo = TransitionDemo.fromIndex(args.getInt(KEY_INDEX));
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(mTransitionDemo.layoutId, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);

        View bgView = ButterKnife.findById(view, R.id.bg_view);
        if (mTransitionDemo.colorId != 0 && bgView != null) {
            bgView.setBackgroundColor(ContextCompat.getColor(getActivity(), mTransitionDemo.colorId));
        }

        final int nextIndex = mTransitionDemo.ordinal() + 1;
        int buttonColor = 0;
        if (nextIndex < TransitionDemo.values().length) {
            buttonColor = TransitionDemo.fromIndex(nextIndex).colorId;
        }
        if (buttonColor == 0) {
            buttonColor = TransitionDemo.fromIndex(0).colorId;
        }

        mBtnNext.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), buttonColor)));
        mTvTitle.setText(mTransitionDemo.title);
    }

    @Override
    protected String getTitle() {
        return "Transition Demos";
    }

    @OnClick(R.id.btn_next) void onNextClicked() {
        final int nextIndex = mTransitionDemo.ordinal() + 1;

        if (nextIndex < TransitionDemo.values().length) {
            getRouter().pushController(getRouterTransaction(nextIndex, this));
        } else {
            getRouter().popToRoot();
        }
    }

    public ControllerChangeHandler getChangeHandler(Controller from) {
        switch (mTransitionDemo) {
            case VERTICAL:
                return new VerticalChangeHandler();
            case CIRCULAR:
                TransitionDemoController demoController = (TransitionDemoController)from;
                return new CircularRevealChangeHandlerCompat(demoController.mBtnNext, demoController.mContainerView);
            case FADE:
                return new FadeChangeHandler();
            case FLIP:
                return new FlipChangeHandler();
            case ARC_FADE:
                return new ArcFadeMoveChangeHandlerCompat();
            case ARC_FADE_RESET:
                return new ArcFadeMoveChangeHandlerCompat();
            case HORIZONTAL:
                return new HorizontalChangeHandler();
            default:
                return null;
        }
    }

    public static RouterTransaction getRouterTransaction(int index, Controller fromController) {
        TransitionDemoController toController = new TransitionDemoController(index);
        ControllerChangeHandler changeHandler = toController.getChangeHandler(fromController);

        return RouterTransaction.builder(toController)
                .pushChangeHandler(changeHandler)
                .popChangeHandler(changeHandler)
                .build();
    }

}
