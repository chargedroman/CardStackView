package com.yuyakaido.android.cardstackview.internal;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.recyclerview.widget.RecyclerView;

import com.yuyakaido.android.cardstackview.Direction;

public class CardStackState implements Parcelable {
    public Status status = Status.Idle;
    public int width = 0;
    public int height = 0;
    public int dx = 0;
    public int dy = 0;
    public int topPosition = 0;
    public int targetPosition = RecyclerView.NO_POSITION;
    public float proportion = 0.0f;


    public CardStackState() {

    }

    public CardStackState(Parcel in) {
        width = in.readInt();
        height = in.readInt();
        dx = in.readInt();
        dy = in.readInt();
        topPosition = in.readInt();
        targetPosition = in.readInt();
        proportion = in.readFloat();
    }

    public static final Creator<CardStackState> CREATOR = new Creator<CardStackState>() {
        @Override
        public CardStackState createFromParcel(Parcel in) {
            return new CardStackState(in);
        }

        @Override
        public CardStackState[] newArray(int size) {
            return new CardStackState[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(width);
        dest.writeInt(height);
        dest.writeInt(dx);
        dest.writeInt(dy);
        dest.writeInt(topPosition);
        dest.writeInt(targetPosition);
        dest.writeFloat(proportion);
    }


    public enum Status {
        Idle,
        Dragging,
        RewindAnimating,
        AutomaticSwipeAnimating,
        AutomaticSwipeAnimated,
        ManualSwipeAnimating,
        ManualSwipeAnimated;

        public boolean isBusy() {
            return this != Idle;
        }

        public boolean isDragging() {
            return this == Dragging;
        }

        public boolean isSwipeAnimating() {
            return this == ManualSwipeAnimating || this == AutomaticSwipeAnimating;
        }

        public Status toAnimatedStatus() {
            switch (this) {
                case ManualSwipeAnimating:
                    return ManualSwipeAnimated;
                case AutomaticSwipeAnimating:
                    return AutomaticSwipeAnimated;
                default:
                    return Idle;
            }
        }
    }

    public void next(Status state) {
        this.status = state;
    }

    public Direction getDirection() {
        if (Math.abs(dy) < Math.abs(dx)) {
            if (dx < 0.0f) {
                return Direction.Left;
            } else {
                return Direction.Right;
            }
        } else {
            if (dy < 0.0f) {
                return Direction.Top;
            } else {
                return Direction.Bottom;
            }
        }
    }

    public float getRatio() {
        int absDx = Math.abs(dx);
        int absDy = Math.abs(dy);
        float ratio;
        if (absDx < absDy) {
            ratio = absDy / (height / 2.0f);
        } else {
            ratio = absDx / (width / 2.0f);
        }
        return Math.min(ratio, 1.0f);
    }

    public boolean isSwipeCompleted() {
        if (status.isSwipeAnimating()) {
            if (topPosition < targetPosition) {
                if (width < Math.abs(dx) || height < Math.abs(dy)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean canScrollToPosition(int position, int itemCount) {
        if (position == topPosition) {
            return false;
        }
        if (position < 0) {
            return false;
        }
        if (itemCount < position) {
            return false;
        }
        if (status.isBusy()) {
            return false;
        }
        return true;
    }

}
