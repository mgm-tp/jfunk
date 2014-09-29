package com.mgmtp.jfunk.core.ui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class BorderPaneExample extends Application
{
	private BorderPane root;

	@Override
	public void start(final Stage primaryStage) throws Exception
	{
		root = new BorderPane();
		root.setTop(getMenu());
		root.setRight(getRightHBox());
		root.setBottom(getFooter());
		root.setLeft(getLeftHBox());
		root.setCenter(getCenterPane());

		Scene scene = new Scene(root, 900, 500);
		primaryStage.setTitle("BorderPane Example");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	private MenuBar getMenu()
	{
		MenuBar menuBar = new MenuBar();

		Menu menuFile = new Menu("File");
		Menu menuEdit = new Menu("Edit");
		Menu menuHelp = new Menu("Help");
		menuBar.getMenus().addAll(menuFile, menuEdit, menuHelp);

		return menuBar;
	}

	private HBox getRightHBox()
	{
		HBox hbox = new HBox();

		VBox vbox = new VBox(50);
		vbox.setPadding(new Insets(0, 20, 0, 20));
		vbox.setAlignment(Pos.CENTER);

		vbox.getChildren().addAll(new Text("Additional Info 1"),
				new Text("Additional Info 2"), new Text("Additional Info 3"));
		hbox.getChildren().addAll(new Separator(Orientation.VERTICAL), vbox);

		return hbox;
	}

	private HBox getLeftHBox()
	{
		HBox hbox = new HBox();

		VBox vbox = new VBox(10);
		vbox.setPadding(new Insets(10));

		Text text = new Text("Navigation");
		text.setFont(Font.font("Helvetica", FontWeight.BOLD, 20));

		VBox vboxText = new VBox(10);
		for (int i = 1; i <= 10; i++)
		{
			vboxText.getChildren().add(new Text("Category " + i));
		}
		vboxText.setTranslateX(10);

		vbox.getChildren().addAll(text, vboxText);
		hbox.getChildren().addAll(vbox, new Separator(Orientation.VERTICAL));

		return hbox;
	}

	private VBox getFooter()
	{
		VBox vbox = new VBox();

		HBox hbox = new HBox(20);
		hbox.setPadding(new Insets(5));
		hbox.setAlignment(Pos.CENTER);

		hbox.getChildren().addAll(new Text("Footer Item 1")
		, new Text("Footer Item 2"), new Text("Footer Item 3"));
		vbox.getChildren().addAll(new Separator(), hbox);

		return vbox;
	}

	private StackPane getCenterPane()
	{
		StackPane stackPane = new StackPane();
		stackPane.setAlignment(Pos.CENTER);

		Rectangle rec = new Rectangle();
		rec.setFill(Color.DODGERBLUE);
		rec.widthProperty().bind(stackPane.widthProperty().subtract(50));
		rec.heightProperty().bind(stackPane.heightProperty().subtract(50));

		stackPane.getChildren().addAll(rec);

		return stackPane;
	}

	public static void main(final String[] args)
	{
		Application.launch(args);
	}
}