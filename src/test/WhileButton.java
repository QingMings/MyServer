import javafx.animation.AnimationTimer;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * 按钮按下时候一直执行 action事件
 */
public class WhileButton extends Button {
    private ExecuteTimer timer = new ExecuteTimer(this);

    public WhileButton() {
        this.addEventFilter(MouseEvent.ANY, event -> {
            if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
                timer.start();
            } else {
                timer.stop();
            }
        });
    }

    class ExecuteTimer extends AnimationTimer {
        private long lastUpdate = 0L;
        private Button mbtn;

        public ExecuteTimer(Button button) {
            this.mbtn = button;
        }

        @Override
        public void handle(long now) {
            if (this.lastUpdate > 100) {
                if (mbtn.isPressed()) {
                    mbtn.fire();
                }
            }
            this.lastUpdate = now;
        }
    }
}
