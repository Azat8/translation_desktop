package mkh.azat;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.util.Duration;
import mkh.azat.frames.MainFrame;
import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.darkprograms.speech.translator.GoogleTranslate;
import tray.animations.AnimationType;
import tray.notification.NotificationType;
import tray.notification.TrayNotification;

import javax.swing.*;

import static java.lang.Thread.sleep;

public class Main implements ClipboardOwner, NativeKeyListener {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    int lastKey;

    final int WINK_CTRL = 29;
    final int WINK_C = 46;
    final int WINK_INSERT = 3666;
    final int WINK_SPACE = 57;

    public void displayNotification(String firstTrans, String secondTrans) throws AWTException, IOException {
        TrayNotification tray = new TrayNotification();
        AnimationType type = AnimationType.POPUP;
        tray.setAnimationType(type);

        String path = "/images/icon16.png";
        URL resource = Main.class.getResource(path);

        if (resource == null) {
            throw new IOException("Image not found: " + path);
        }

        Image image = Toolkit.getDefaultToolkit().createImage(resource);

        tray.setTitle(firstTrans);
        tray.setMessage(secondTrans);
        tray.setNotificationType(NotificationType.INFORMATION);
        tray.showAndDismiss(Duration.millis(300));
//        TrayIcon icon = new TrayIcon(image, "Translate app");
//        icon.setImageAutoSize(true);
//        icon.setToolTip(firstTrans);
//        icon.setImage(image);
//        tray.add(icon);
//        icon.displayMessage(firstTrans, secondTrans, TrayIcon.MessageType.INFO);
    }

    void getClipboardData() {
        Transferable content = this.clipboard.getContents(null);

        this.clipboard.setContents(content, this);
        if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) content.getTransferData(DataFlavor.stringFlavor);
                System.out.println("Text: " + text);

                String textRU = GoogleTranslate.translate("ru", text);
                String textHY = GoogleTranslate.translate("hy", text);

                this.displayNotification(textRU, textHY);
                System.out.println("Translated: " + textRU + ", " + textHY);
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }


//    private volatile TrayNotification tray;
//
//    @BeforeClass
//    public static void initializeJavaFX() throws InterruptedException {
//        final CountDownLatch latch = new CountDownLatch(1);
//        SwingUtilities.invokeLater();
//
//        new JFXPanel(); // initializes JavaFX environment
//        latch.countDown();
//        latch.await();
//    }
//
//    @AfterClass
//    public static void shutdownJavaFX() {
//        Platform.exit();
//    }
//
//    @Before
//    public void initializeTray() {
//        Platform.runLater(() -> tray = new TrayNotification());
//    }

    public static void main(String[] args) throws IOException, UnsupportedFlavorException, InterruptedException, NativeHookException, AWTException {
        final Main main = new Main();

        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }

        GlobalScreen.addNativeKeyListener(main);

        LogManager.getLogManager().reset();

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);

        main.displayNotification("Quick translation launched", "Happy learning :)");

//        MainFrame mainFrame = new MainFrame();
//        mainFrame.render();

        Object o = new Object();
        synchronized (o) {o.wait();}
    }

    void regainOwnership(Transferable t) {
        clipboard.setContents(t, this);
    }

    public void lostOwnership(Clipboard clip, Transferable t) {
        System.out.println("Owner was changed");

        try {
            sleep(200);
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }

        Transferable contents = clip.getContents(this);
        regainOwnership(contents);
    }

    @Override
    public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
        int keyCode = nativeKeyEvent.getKeyCode();
        if(this.lastKey == WINK_CTRL && (keyCode == WINK_C ||
                                        keyCode == WINK_INSERT ||
                                        keyCode == WINK_SPACE)) {
            try {
                sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.getClipboardData();
            System.out.println("Was handled copy action");
        }

        this.lastKey = keyCode;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
