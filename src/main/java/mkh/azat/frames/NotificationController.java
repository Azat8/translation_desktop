package mkh.azat.frames;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogManager;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.keyboard.NativeKeyEvent;
import org.jnativehook.keyboard.NativeKeyListener;

import com.darkprograms.speech.translator.GoogleTranslate;

import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import mkh.azat.api.API;

public class NotificationController implements Initializable, ClipboardOwner  {
    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    int lastKey;

    final int WINK_CTRL = 29;
    final int WINK_C = 46;
    final int WINK_INSERT = 3666;
    final int WINK_SPACE = 57;
   
    Boolean loading = false;
    
    @FXML 
    private ImageView closeImg, saveImg;
    
	@FXML
	private Label originalText, firstTrans, secondTrans;
	
    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    Stage stage;

    public void setStage(Stage stage) {
		this.stage = stage;
	}
    
	public void setOriginalTextString(String originalTextString) {
		originalText.setText(originalTextString);
	}
    
	public void setFirstTransString(String firstTransString) {
		firstTrans.setText(firstTransString);
	}
	
	public void setSecondTransString(String secondTransString) {
		secondTrans.setText(secondTransString);
	}
    
    @FXML
    void initialize() {
    	
    }

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		
        try {
            GlobalScreen.registerNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        
        class KeyPressHandler implements NativeKeyListener {
        	NotificationController ui;
        	
        	KeyPressHandler(NotificationController ui) {
        		System.out.println("ui: " + ui);
        		this.ui = ui;
        	}
        	
            @Override
            public void nativeKeyTyped(NativeKeyEvent nativeKeyEvent) {

            }

            @Override
            public void nativeKeyPressed(NativeKeyEvent nativeKeyEvent) {
            	final int keyCode = nativeKeyEvent.getKeyCode();
            	  Platform.runLater(new Runnable () {
            		  @Override
            		  public void run() {

                      	
                          if(ui.lastKey == WINK_CTRL && (keyCode == WINK_C ||
                                                          keyCode == WINK_INSERT ||
                                                          keyCode == WINK_SPACE)) {
                              try {
                                  Thread.sleep(200);
                              } catch (InterruptedException e) {
                                  e.printStackTrace();
                              }
                              try {
                  				ui.getClipboardData();
                  			} catch (InterruptedException e) {
                  				// TODO Auto-generated catch block
                  				e.printStackTrace();
                  			}
                              ui.stage.close();
                              ui.stage.show();
                              System.out.println("Was handled copy action +++");
                          }

                          ui.lastKey = keyCode;
            		  }
                  });
            	  
            }

            @Override
            public void nativeKeyReleased(NativeKeyEvent nativeKeyEvent) {

            }

        }
        
        saveImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		    	 if(!loading) {
		    		 
		    		 InputStream starActive = getClass().getResourceAsStream("/images/star-active.png");
		    		 System.out.println(starActive);
		    		 Image img = new Image(starActive);
		    		 saveImg.setImage(img);
		    		 
		    		 try {
		    			 save();
		    		 } catch (Exception e) {
		    			 e.printStackTrace();
		    		 }
		    		 
		    		 loading = true;
		    	 }
		     }		     
		});
        
        closeImg.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {

		     @Override
		     public void handle(MouseEvent event) {
		    	 close();
		     }		     
		});
        
        KeyPressHandler handler = new KeyPressHandler(this);
        GlobalScreen.addNativeKeyListener(handler);

        LogManager.getLogManager().reset();

        Logger logger = Logger.getLogger(GlobalScreen.class.getPackage().getName());
        logger.setLevel(Level.OFF);
	}
	
	public void regainOwnership(Clipboard c, Transferable t) {
        c.setContents(t, this);
    }

    public void lostOwnership(Clipboard clip, Transferable t) {
        try {
            Thread.sleep(1000);
        } catch(Exception e) {
            System.out.println("Exception: " + e);
        }

        Transferable transferable = clip.getContents(this);
        this.regainOwnership(clipboard, transferable);
    }
    
    void getClipboardData() throws InterruptedException {
    	InputStream star = getClass().getResourceAsStream("/images/star.png");
		System.out.println(star);
		Image img = new Image(star);
		saveImg.setImage(img);
    	
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

    public void displayNotification(String text, String textRU, String textHY) {
    	
    	this.setOriginalTextString(text);
    	this.setFirstTransString(textRU);
    	this.setSecondTransString(textHY);
    }
    
    public void save() throws Exception {
    	HashMap<String, String> translationList = new HashMap<String, String>();
    	translationList.put("original", this.originalText.getText());
    	translationList.put("ru", this.firstTrans.getText());
    	translationList.put("hy", this.secondTrans.getText());
    	
    	API.saveTranslation(translationList);
    }
    
    public void close() {
    	this.stage.close();
    }
}
