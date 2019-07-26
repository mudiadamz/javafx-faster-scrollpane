import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class CustomizeScrollSpeedApplication extends Application {
    private VBox rootNode = new VBox();
    private ScrollPane mainScrollPane = new ScrollPane();
    private VBox vb = new VBox();
    private String [] imageNames = new String [] {"fw1.jpg", "fw2.jpg", "fw3.jpg", "fw4.jpg", "fw0.jpg"};
    private Scene scene;

    @Override
    public void start(Stage stage) {
        scene = new Scene( rootNode,500,500 );
        stage.setScene(scene);
        stage.setTitle("Scroll Pane");
        rootNode.getChildren().addAll(mainScrollPane);
        VBox.setVgrow(mainScrollPane, Priority.ALWAYS);

        HBox hBox = null;
        for (int i = 0; i < 100; i++) {
            int index = i %5;

            Image image
                     = new Image(getClass().getResourceAsStream("flowers/"+ imageNames[index]));
            ImageView imageView = new ImageView(image);
            imageView.setFitWidth(100);
            imageView.setPreserveRatio(true);

            if(i%13==0){
                hBox = new HBox();
                vb.getChildren().add(hBox);
            }else {
                //intellij thinks this hBox always null, so funny
                //don't believe that
                if(hBox!=null)
                    hBox.getChildren().add(imageView);
            }
        }
        mainScrollPane.setContent(vb);
        setTheGoddammitScrollSpeedBecauseTheDefaultJavaFxIsSoFuckingLame(mainScrollPane);

        stage.show();
        stage.setMaximized(true);

    }

    private void setTheGoddammitScrollSpeedBecauseTheDefaultJavaFxIsSoFuckingLame(final ScrollPane customScrollPane){
        //To get nodes height, first you need to do this things
        rootNode.applyCss();
        rootNode.layout();

        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        //get the scrollPane height,
        double vpHeight = bounds.getHeight();
        double contentHeight = customScrollPane.getContent().getBoundsInLocal().getHeight();

        double ratio = (vpHeight/contentHeight);

        System.out.println(ratio);

        // the bigger the slower
        final double[] MAX_VERTICAL = new double[1];
        if (ratio>0.9){
            MAX_VERTICAL[0] = 1;
        }else if (ratio>0.7){
            MAX_VERTICAL[0] = 2;
        }else  {
            MAX_VERTICAL[0] = 10;
        }

        // set the speed of scroll, again it's not pixels, it's just numbers
        // How many scrolls you want = MAX_VERTICAL/SCROLL_SPEED
        final double SCROLL_SPEED = ratio;

        //set either the MAX_VERTICAL or SCROLL speed whichever for you to feel it better
        customScrollPane.setVmax(MAX_VERTICAL[0]);
        final double[] i = {0};
        customScrollPane.addEventFilter(ScrollEvent.SCROLL,new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() != 0) {
                    boolean isScrollDown = event.getDeltaY()<0;
                    event.consume(); //prevent the default scrolling
                    double newPos = i[0];
                    if(isScrollDown){
                        newPos += SCROLL_SPEED;
                    }else{
                        newPos -= SCROLL_SPEED;
                    }
                    newPos = newPos<0?0:newPos; //prevent to be negative
                    newPos = newPos>MAX_VERTICAL[0]?MAX_VERTICAL[0]:newPos; //prevent to be over max
                    i[0] = newPos;
                    customScrollPane.setVvalue(newPos);

                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
