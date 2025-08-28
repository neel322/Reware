package HealthTracker;

import javax.swing.*;

public class NavigationManager {
    public static JFrame[] pageHistory;
    private static int top;
    private static final int INITIAL_CAPACITY = 10;

    static {
        pageHistory = new JFrame[INITIAL_CAPACITY];
        top = -1;
    }

    public static void pushPage(JFrame newFrame) {
        if (top == pageHistory.length - 1) {
            resizeArray();
        }

        top++;
        pageHistory[top] = newFrame;

        System.out.println("Pushed: " + newFrame.getTitle() + ", Top: " + top);
    }

    public static JFrame popPage() {
        if (isEmpty()) {
            System.out.println("Stack is empty - cannot pop");
            return null;
        }


        JFrame topFrame = pageHistory[top];

        pageHistory[top] = null;
        top--;

        System.out.println("Popped: " + (topFrame != null ? topFrame.getTitle() : "null") + ", Top: " + top);
        return topFrame;
    }

    private static void resizeArray() {
        int newCapacity = pageHistory.length * 2;
        JFrame[] newArray = new JFrame[newCapacity];

        for (int i = 0; i <= top; i++) {
            newArray[i] = pageHistory[i];
        }

        pageHistory = newArray;
        System.out.println("Resized array to capacity: " + newCapacity);
    }

    public static boolean isEmpty() {
        return top == -1;
    }
}
