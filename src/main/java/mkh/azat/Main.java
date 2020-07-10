package mkh.azat;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.darkprograms.speech.translator.GoogleTranslate;

import javafx.application.Application;
import mkh.azat.frames.MainFrame;

public class Main extends Thread implements ClipboardOwner, NativeKeyListener {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    int lastKey;

    final int WINK_CTRL = 29;
    final int WINK_C = 46;
    final int WINK_INSERT = 3666;
    final int WINK_SPACE = 57;
    
    public void displayNotification(String originalText, String firstTrans, String secondTrans) throws AWTException, IOException {
    	String args[] = {originalText, firstTrans, secondTrans};
    	Application.launch(MainFrame.class, args);
    	
//    	MainFrame.main(args);    	
    }

    void getClipboardData() throws InterruptedException {
        Transferable transferable = this.clipboard.getContents(this);
        this.regainOwnership(clipboard, transferable);

        if (transferable.isDataFlavorSupported(DataFlavor.stringFlavor)) {
            try {
                String text = (String) transferable.getTransferData(DataFlavor.stringFlavor);
                System.out.println("Text: " + text);

                String textRU = GoogleTranslate.translate("ru", text);
                String textHY = GoogleTranslate.translate("hy", text);

                this.displayNotification(text, textRU, textHY);
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

        main.displayNotification("Hi,", "Quick translation launched", "Happy learning :)");
        
        while(true);
    }

    public void regainOwnership(Clipboard c, Transferable t) {
        c.setContents(t, this);
    }

    public void lostOwnership(Clipboard clip, Transferable t) {
        try {
            sleep(1000);
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }

        Transferable transferable = clip.getContents(this);
        this.regainOwnership(clipboard, transferable);
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
            try {
				this.getClipboardData();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            System.out.println("Was handled copy action");
        }

        this.lastKey = keyCode;
    }

    @Override
    public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

    }
}
