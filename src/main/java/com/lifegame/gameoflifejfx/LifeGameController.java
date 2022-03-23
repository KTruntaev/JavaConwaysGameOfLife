package com.lifegame.gameoflifejfx;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.paint.Color;

/*
 *
 *      TODO:
 *       0. Implement section rendering         DONE
 *       0. Implement grid scrolling            DONE
 *
 *       1. Upgrade the CSS styling
 *
 *       2. Implement drawing mode - After the initial upload
 *
 *       3. A) Calculate the mouse delta, do simple geometry to find the vertical and horizontal components
 *       3. Implement section selection by dragging on the canvas and allowing it to zoom into the selected area
 *
 *       4. Implement the sparse matrix dictionary properly   DELAYED
 *
 */

public class LifeGameController {

    @FXML
    private Canvas displayGrid;

    @FXML
    private Label generationLabel;

    @FXML
    private Slider sizeSlider;
    @FXML
    private Slider rowSlider;
    @FXML
    private Slider colSlider;
    @FXML
    private Slider upsSlider;
    @FXML
    private Button startButton;
    @FXML
    private Button oneGenButton;

    private final int size = 500;
    private Grid environment = new Grid(size, size);

    private int generation;
    private boolean runSim;
    private short ups = 35;  // updates per second
    // currently the default value is 35 ups

    private int sectorSize = 125;
    // sector row and sector col are the positions of the top left corner of the sector
    private int sectorRow;
    private int sectorCol;

    // button methods **************************************************************************************************

    @FXML
    protected void onOneStepButtonClick() {
        long executionTime = System.currentTimeMillis();

        System.out.printf("\nGeneration %s Cells:\n", generation);

        environment.update();
        generation++;

        System.out.println("Execution Time was: " + (System.currentTimeMillis() - executionTime));

        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                generationLabel.setText("Generation: " + generation);
                displayLifeStates();

            }
        });
    }

    @FXML
    protected void onStartButtonClick() {
        // disables the button to avoid creating multiple threads
        startButton.disableProperty().set(true);
        // disables the +1 generation button in order to avoid concurrency issues
        oneGenButton.disableProperty().set(true);

        runSim = true;

        Thread oneStep = new Thread(new Runnable() {
            @Override
            public void run() {
                while (runSim) {

                    onOneStepButtonClick();

                    try {
                        Thread.sleep(1000 / ups);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        //oneStep.setDaemon(true);
        oneStep.start();
    }

    @FXML
    protected void onPauseButtonClick() {
        // allow the user to continue the thread
        startButton.disableProperty().set(false);
        oneGenButton.disableProperty().set(false);
        runSim = false;
    }

    @FXML
    protected void onRegenButtonClick() {
        generation = 0;
        generationLabel.setText("Generation: " + generation);

        environment = new Grid(size, size);
        clearCanvas();
        displayLifeStates();
    }

    // slider methods **************************************************************************************************

    @FXML
    protected void sectorRowUpdate() {
        sectorRow = (int) rowSlider.getValue();
    }

    @FXML
    protected void sectorColumnUpdate() {
        sectorCol = (int) colSlider.getValue();
    }

    /**
     * Updates the size of the grid visible to the user,
     * as well as changing the max values of sliders to avoid going out of bounds
     */
    @FXML
    protected void sectorSizeUpdate() {
        sectorSize = (int) sizeSlider.getValue();

        rowSlider.setMax(size - sectorSize);
        if (rowSlider.getValue() >= rowSlider.getMax()) {
            rowSlider.setValue(rowSlider.getMax());
            sectorRowUpdate();
        }

        colSlider.setMax(size - sectorSize);
        if (colSlider.getValue() >= colSlider.getMax()) {
            colSlider.setValue(colSlider.getMax());
            sectorColumnUpdate();
        }
    }

    @FXML
    protected void upsUpdate() {
        ups = (short) upsSlider.getValue();
    }

    // canvas methods **************************************************************************************************

    public void clearCanvas() {
        displayGrid.getGraphicsContext2D().clearRect(0, 0, displayGrid.getHeight(), displayGrid.getWidth());
    }

    // draw method
    public void displayLifeStates() {

        //sectorSize = (int)sizeSlider.getValue();
        //sectorRow = 0;  // temp
        //sectorCol = 0;  // temp

        Cell[][] gridLifeStates = environment.getCellGrid();
        Cell[][] lifeStates = new Cell[sectorSize][sectorSize];             // sector life states

        for (int i = 0; i < lifeStates.length; i++) {
            for (int j = 0; j < lifeStates[i].length; j++) {
                lifeStates[i][j] = gridLifeStates[i + sectorRow][j + sectorCol];
            }
        }

        double scale = 50.0 / lifeStates.length * 10.0;
        GraphicsContext gc = displayGrid.getGraphicsContext2D();

        gc.setGlobalAlpha(.25);

        //gc.applyEffect(new Bloom(0.1));

        for (int i = 0; i < lifeStates.length; i++) {
            for (int j = 0; j < lifeStates[0].length; j++) {
                if (lifeStates[i][j].getLifeState()) {
                    Cell temp = lifeStates[i][j];
                    gc.setFill(Color.rgb(temp.getRGB()[0], temp.getRGB()[1], temp.getRGB()[2]));
                    gc.fillRect(j * scale, i * scale, scale, scale);
                } else {
                    gc.clearRect(j * scale, i * scale, scale, scale);
                }
            }
        }
    }
}