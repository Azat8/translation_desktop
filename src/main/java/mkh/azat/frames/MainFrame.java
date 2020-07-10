package mkh.azat.frames;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.StageStyle;

public class MainFrame extends Application {
    public static void main(String args[]) {
    	launch(args);
    }

	@Override
	public void start(Stage primaryStage) throws Exception {
//		Parameters params = getParameters();
//			
////	    String originalTextString = params.getRaw().get(0);
////	    String firstTransString = params.getRaw().get(1);
////	    String secondTransString = params.getRaw().get(2);
////	   
	    String originalTextString = "Test";
	    String firstTransString = "Test";
	    String secondTransString = "Test";
	   
	    
	    FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/notification.fxml"));
	    Parent root = loader.load();
		NotificationController controller = loader.getController();
		try {
			System.out.println(controller);
			controller.setOriginalTextString(originalTextString);
			controller.setFirstTransString(firstTransString);
			controller.setSecondTransString(secondTransString);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		
		Scene scene = new Scene(root, 400, 400);
		scene.getStylesheets().add("notification.css");
		primaryStage.setAlwaysOnTop(true);
		Platform.setImplicitExit(false);
		controller.setStage(primaryStage);
		primaryStage.setScene(scene);
		primaryStage.initStyle(StageStyle.UNDECORATED);
		primaryStage.setX(width - 400);
		primaryStage.setY(height - 270);
		primaryStage.setResizable(false);
		primaryStage.show();
	}
}