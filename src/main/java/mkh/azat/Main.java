package mkh.azat;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.darkprograms.speech.translator.GoogleTranslate;

import static java.lang.Thread.sleep;

public class Main implements ClipboardOwner, NativeKeyListener {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    int lastKey;

    final int WINK_CTRL = 29;
    final int WINK_C = 46;
    final int WINK_INSERT = 3666;

    public void displayNotification(String firstTrans, String secondTrans) throws AWTException, IOException {
        SystemTray tray = SystemTray.getSystemTray();
        String path = "/images/icon16.png";
        URL resource = Main.class.getResource(path);

        if (resource == null) {
            throw new IOException("Image not found: " + path);
        }

        Image image = Toolkit.getDefaultToolkit().createImage(resource);

        TrayIcon icon = new TrayIcon(image, "Translate app");
        icon.setImageAutoSize(true);
        icon.setToolTip(firstTrans);
        icon.setImage(image);
        tray.add(icon);
        icon.displayMessage(firstTrans, secondTrans, TrayIcon.MessageType.INFO);
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

        Object o = new Object();
        synchronized (o) {o.wait();}
    }

    void regainOwnership(Transferable t) {
        clipboard.setContents(t, this);
    }

    public void lostOwnership(Clipboard clip, Transferable t) {
        System.out.println("Owner was changed");

        try {
            sleep(30);
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
        if(this.lastKey == WINK_CTRL && (nativeKeyEvent.getKeyCode() == WINK_C || nativeKeyEvent.getKeyCode() == WINK_INSERT)) {
            try {
                sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            this.getClipboardData();
            System.out.println("Was handled copy action");
        }

        this.lastKey = nativeKeyEvent.getKeyCode();
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
