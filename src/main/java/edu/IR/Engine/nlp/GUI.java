package edu.IR.Engine.nlp;
import edu.IR.Engine.nlp.*;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.paint.*;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.image.Image ;
import javafx.scene.image.ImageView;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import javafx.scene.layout.Background;
import org.joda.time.field.FieldUtils;

import static java.nio.file.Files.deleteIfExists;

public class GUI extends Application {

    Stage window;
    Scene scene,scene2;
    //Scene dictionaryScene, cacheScene;
    ListView<String> dictionary;
    ListView <String>cache;
    private boolean doStemming=true;
    TextField postingInput;
    TextField loadInput;
    TextField loadInput2;
    TextField loadInput3;
    TextField saveInput;
    TextField corpusInput;
    TextField file_query_input;
    String pathToSave="";
    String pathToLoad="";
    String s="";
    //Map<String, TermCache> loadCache;
    String pathToPosting="";
    String pathToCorpus="";
    ReadFile r;
    Parse P;
    Indexer indexer;
    TextField queryInput;
    TextField queryFileInput;
    long totalTime;
    boolean finish=false;
    static Map<String,String> stopword ;
    Map<String, List<TermData>> loadDictinary;
    int numOfDocIndex;
    int numOfUniqTerms;
    //Map<String,List<TermData>>dict;
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        window = primaryStage;
        window.setTitle("Welcome to our search engine! ");
        GridPane grid = new GridPane();
        grid.setPadding(new Insets( 10, 20, 10, 20));
        grid.setVgap(10);
        grid.setHgap(10);

        ImageView iv=new ImageView();
        Image image = new Image("file:search-engine-189880.jpg");
        iv.setImage(image);
        iv.setFitWidth(510);
        iv.setFitHeight(200);
        iv.setImage(image);
        GridPane.setConstraints(iv, 1, 0);


        //corpus Label - constrains use (child, column, row)
        Label corpusLabel = new Label("corpus:");
        corpusLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(corpusLabel, 0, 1);

        //corpos path Input
        corpusInput = new TextField();
        corpusInput.setPromptText("corpus path here");
        corpusInput.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(corpusInput, 1, 1);
        //browse button
        Button browseButton2 = new Button("browse");
        browseButton2.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(browseButton2, 2, 1);
        browseButton2.setOnAction(e->browser());

        //posting Label
        Label postingLabel = new Label("posting files:");
        postingLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(postingLabel, 0, 2);

        //posting path Input
        postingInput = new TextField();
        postingInput.setPromptText("posting path here");
        postingInput.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(postingInput, 1, 2);

        //browse button
        Button browseButton = new Button("browse");
        browseButton.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(browseButton, 2, 2);
        browseButton.setOnAction(e-> browserPosting());

        //Stemming
        Label stemmLabel = new Label("Do you want to preform Stemming?");
        stemmLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(stemmLabel, 1, 3);
        //ToggleGroup stemming = new ToggleGroup();
        CheckBox stemmerCheck=new CheckBox("Stemming?");
        stemmerCheck.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(stemmerCheck, 2, 3);

        //Start
        Button startButton = new Button("START");
        startButton.setStyle("-fx-font-weight: bold");
        startButton.setStyle("-fx-background-color: #90EE90");
        GridPane.setConstraints(startButton, 1, 4);
        startButton.setOnAction(e -> {
            try {
                StartButton(corpusInput.getText(), postingInput.getText(), stemmerCheck.isSelected());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        });
        startButton.disableProperty().bind(Bindings.createBooleanBinding( () -> !((postingInput.getText()!=null && corpusInput.getText()!=null)),
                postingInput.textProperty(), corpusInput.textProperty()));

        //RESET
        Button resetButton = new Button("RESET");
        resetButton.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(resetButton, 2, 5);
        //reset Label
        Label resetLabel = new Label("To reset the posting and dictionary:");
        resetLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(resetLabel, 1, 5);
        resetButton.setOnAction(e-> {
            try {
                deleteReset();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });


        //Display dictionary
        Button dictionaryDisplayButton = new Button("Dictionary");
        dictionaryDisplayButton.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(dictionaryDisplayButton, 2, 7);
        Label displayDictionaryLabel = new Label("View Dictionary:");
        displayDictionaryLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(displayDictionaryLabel, 1, 7);
        dictionaryDisplayButton.setOnAction(e->{
            try {
                displayDictTable(postingInput.getText(),stemmerCheck.isSelected());
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        //load
        Button browseButton4 = new Button("browse");
        browseButton4.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(browseButton4, 2, 9);
        browseButton4.setOnAction(e-> browserLoad());
//
//        //load the created files
        Button loadButton = new Button("LOAD");
        loadButton.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(loadButton, 4, 9);
        Label loadLabel = new Label("load the Dictionary :");
        loadLabel.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(loadLabel, 0, 9);
        loadButton.setOnAction(e -> {loadFiles();});
        loadInput = new TextField();
        loadInput.setPromptText("load path here");
        loadInput.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(loadInput, 1, 9);


        Button changeScene=new Button("Go to search !");
        changeScene.setStyle("-fx-background-color: #DC143C ");
//        changeScene.setStyle("-fx-font-weight: bold");
        changeScene.setOnAction(event -> window.setScene(scene2));
        GridPane.setConstraints(changeScene, 1, 10);


        GridPane grid2 = new GridPane();
        grid2.setPadding(new Insets( 10, 20, 10, 20));
        grid2.setVgap(10);
        grid2.setHgap(10);

        //1
        ImageView iv2=new ImageView();
        Image image2 = new Image("file:search-engine-189880.jpg");
        iv2.setImage(image2);
        iv2.setFitWidth(510);
        iv2.setFitHeight(200);
        iv2.setImage(image2);
        GridPane.setConstraints(iv2, 1, 0);

        //2
        Label labelStart = new Label("Start Searching");
        labelStart.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(labelStart, 0, 1);
        //check box for semantic
        CheckBox semantic=new CheckBox("with semantic?");
        semantic.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(semantic, 1, 1);

        //3
        Label enter_query = new Label("Enter query:");
        enter_query.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(enter_query, 0, 2);
        loadInput2 = new TextField();
        loadInput2.setPromptText("search");
        loadInput2.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(loadInput2, 1, 2);
        Button runQuery = new Button("Run");
        runQuery.setStyle("-fx-background-color: #4169E1");
        GridPane.setConstraints(runQuery, 2, 2);

        //4
        Label enter_query2 = new Label("get file query:");
        enter_query2.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(enter_query2, 0, 3);
        file_query_input = new TextField();
        file_query_input.setPromptText("file query input");
        file_query_input.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(file_query_input, 1, 3);
        Button browseButton5 = new Button("browse");
        browseButton5.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(browseButton5, 2, 3);
        browseButton5.setOnAction(e-> browser());
        Button runQuery2 = new Button("Run");
        runQuery2.setStyle("-fx-background-color: #4169E1");
        GridPane.setConstraints(runQuery2, 3, 3);

        //5
        Label get5label = new Label("get 5 queries if you want");
        get5label.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(get5label, 1, 4);
        Button get5=new Button("GET 5");
        get5.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(get5, 2, 4);

        //6
        Label enter_save_file = new Label("The path to save file");
        enter_save_file.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(enter_save_file, 0, 5);
        loadInput3 = new TextField();
        loadInput3.setPromptText("file");
        loadInput3.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(loadInput3, 1, 5);
        Button browseButton6 = new Button("browse");
        browseButton6.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(browseButton6, 2, 5);
        browseButton6.setOnAction(e-> browser());
        Button saveQuery = new Button("Save");
        saveQuery.setStyle("-fx-font-weight: bold");
        GridPane.setConstraints(saveQuery, 3, 5);

        Button go_back_Scene=new Button("Go back !");
        go_back_Scene.setStyle("-fx-background-color: #DC143C");
        go_back_Scene.setOnAction(event -> window.setScene(scene));
        GridPane.setConstraints(go_back_Scene, 1, 6);


        grid2.getChildren().addAll(go_back_Scene,iv2,labelStart,semantic,enter_query,loadInput2,runQuery,
                enter_query2,file_query_input,browseButton5,runQuery2,get5,enter_save_file,loadInput3,browseButton6,get5label,saveQuery);

        //Add everything to grid
        grid.getChildren().addAll(corpusLabel, corpusInput, postingLabel, postingInput,browseButton, startButton
                ,stemmerCheck,stemmLabel,resetButton,resetLabel,
                loadButton,loadLabel,browseButton2,dictionaryDisplayButton,displayDictionaryLabel,browseButton4,loadInput,iv,changeScene);


        grid.setStyle("-fx-background-color: #5F9EA0;");
        grid2.setStyle("-fx-background-color: #5F9EA0;");
        scene=new Scene(grid,850,550);
        scene2=new Scene(grid2,850,550,Color.CHOCOLATE);
        window.setScene(scene);
        window.show();
    }
    //When button is clicked, handle() gets called
    //Button click is an ActionEvent (also MouseEvents, TouchEvents, etc...)

    public void StartButton (String s1, String s2, boolean box1) throws Exception
    {
        long startTime = System.currentTimeMillis()/1000;
        numOfDocIndex=0;
        if(s1.length()>0&&s2.length()>0) {//the fields are filled

            pathToCorpus = s1;
            pathToPosting = s2;
            String fullPath="";
            //change secene to alert and back to the main window to let write again
            if (box1) {
                doStemming = true;
                //stemming
                fullPath=pathToPosting + "\\yesStem\\";
            } else {
                doStemming = false;
                //no stemming
                fullPath=pathToPosting + "\\noStem\\";
            }

            File dir=new File(fullPath);
            if(!dir.exists()){
                dir.mkdir();
            }
            Map<String, List<TermData>> lastDictionaryToView = null;
            indexer = new Indexer(fullPath);
            ReadFile readFile = new ReadFile();
            List<String> files = readFile.getAllFiles(pathToCorpus);
            Integer courpus_size = files.size();
            Integer fileCounter = 0;
            for (int i =0; i<0; i++){
                files.remove(0);
                fileCounter++;
            }
            String text = null;
            String path = System.getProperty("user.dir")+"/stop_words.txt";
            File pathofstopword=new File(path);
            BufferedReader br = new BufferedReader(new FileReader(pathofstopword));
            String st;
            stopword = new HashMap<>();// why save stop?
            while ((st = br.readLine()) != null){
                stopword.put(st,"");
            }

            Parse parser = new Parse(stopword,doStemming);

            for (String filePath : files) {
                double percent = (0.0 +  ++fileCounter ) / courpus_size*100;
                System.out.println(String.format("%.2f", percent)  + "% File: " + fileCounter + " " + filePath);
                text = readFile.openFile(filePath);
                //xml to documents
                IRDocument[] fileDocs = readFile.parseXML(text);
                //parse documents
                System.out.println("parsing");
                if (true) {
                    for (IRDocument doc : fileDocs) {
                        ParseResult parseResult = parser.parseDocument(doc);
                        DocumentData documentData = parseResult.documentData;
                        DocumentTerms documentTerms = parseResult.documentTerms;
                        documentTerms.sort();
                        indexer.addTerms(documentTerms, documentData.docID);
                        indexer.addDocument(documentData);

                        if (indexer.isMemoryFull()) {
                            indexer.savePosting();
                        }
                        numOfDocIndex++;

                    }
                }
            }
            ///final dump
            indexer.savePosting();
            // merge sort - LIMITED to file size (logical,virtual,string,terms,lists)
            indexer.merge();
            indexer.createDictionary();
            indexer.saveDocuments();
            getDictionaryTermGui();
            int i = 0;
            long endTime = System.currentTimeMillis()/1000;
            totalTime = endTime - startTime;
            System.out.println(totalTime);
            finish = true;
            finishData();

        }
        else {// the fields are missing
            //change scene to alert and back to the main window to let write again
            try {
                AlertBox.display("Missing Input", "Error: no paths had been written!");
            } catch (Exception e) {

            }
        }

    }

    public ObservableList<String> getDictionaryTermGui() throws Exception {//get the items for the dictionary
        dictionary =new ListView<>();
        SortedSet<String> sortedKeys;
        ObservableList<String> termsDictionary= FXCollections.observableArrayList();
        List<String> dict;
        indexer.createDictionary();
        if(indexer!=null) {
            dict = indexer.lastDictionaryToView;//change to public for dictionary in indexer
//            sortedKeys = new TreeSet<>(dict.keySet());
        }
        else {
            dict = new ArrayList<>();
        }
        for(int i=0;i<dict.size();i++){
            termsDictionary.add(dict.get(i));
        }
        dictionary.setItems(termsDictionary);
        return termsDictionary;
    }

    public void displayDictTable(String s2,boolean box1) throws IOException {//opens another window with the dictionary table display

        //https://stackoverflow.com/questions/27414689/a-java-advanced-text-logging-pane-for-large-output
        String fullPath="";
        pathToPosting=s2;
        if (box1) {
            doStemming = true;
            //stemming
            fullPath=pathToPosting + "\\yesStem\\";
        } else {
            doStemming = false;
            //no stemming
            fullPath=pathToPosting + "\\noStem\\";
        }
        dictionary = new ListView<>();
        //dictionary.setItems(getDictionaryTermGui());
        ObservableList<String> termsDictionary= FXCollections.observableArrayList();
        List<String> dict;
        if(indexer!=null)
            dict= indexer.getDictionaryForView(fullPath+"\\dictionary.txt");//change to public for dictionary in indexer
        else
            dict=new ArrayList<>();

        for(int i=0;i<dict.size();i++){
            termsDictionary.add(dict.get(i));
        }

        dictionary.setItems(termsDictionary);

        VBox vBox = new VBox();
        vBox.getChildren().addAll(dictionary);
        Scene dictionaryScene=new Scene(vBox);
        Stage dicwindow = new Stage();

        //Block events to other windows
        dicwindow.initModality(Modality.APPLICATION_MODAL);
        dicwindow.setTitle("THE DICTIONARY");
        dicwindow.setMinWidth(250);
        dicwindow.setScene(dictionaryScene);
        dicwindow.show();
    }



    public void loadFiles()  {

    }


    public void browser(){
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory((new File("C:\\")));
            File selectedFile = dc.showDialog(null);
            s = selectedFile.getAbsolutePath();
            corpusInput.setText(s);
            pathToCorpus = s;
        }
        catch(Exception e) {

        }

    }
    public void browserPosting()
    {
        try {
            DirectoryChooser dc = new DirectoryChooser();
            dc.setInitialDirectory((new File("C:\\")));
            File selectedFile = dc.showDialog(null);
            s = selectedFile.getAbsolutePath();
            postingInput.setText(s);
            pathToPosting = s + "\\";
        }
        catch(Exception e) {

        }
    }
    public void browserLoad()
    {
        DirectoryChooser dc=new DirectoryChooser();
        dc.setInitialDirectory((new File("C:\\")));
        File selectedFile=dc.showDialog(null);
        s=selectedFile.getAbsolutePath();
        loadInput.setText(s);
        pathToLoad=s;
    }

    public void deleteReset() throws IOException {
        dictionary = null;
        cache = null;
        String noStem=pathToPosting+"\\noStem";
        String yesStem=pathToPosting+"\\yesStem";
        try {
            File file = new File(yesStem+"/dictionary.txt");
            File file2 = new File(yesStem+"/documents.txt");
            File file3= new File(yesStem+"/post.txt");
            File fil = new File(noStem+"/dictionary.txt");
            File fil2 = new File(noStem+"/documents.txt");
            File fil3= new File(noStem+"/post.txt");

            try {if(fil.exists())
                fil.delete();
                if(fil2.exists())
                    fil2.delete();
                if(fil3.exists())
                    fil3.delete();

            }catch (Exception e){

            }

            try {
                if(file.exists())
                    file.delete();
                if(file2.exists())
                    file2.delete();
                if(file3.exists())
                    file3.delete();
            } catch (Exception e) {
            }
            try {


            } catch (Exception e) {

            }

        }
        catch(Exception e){}
        AlertBox.display("Reset","The dictionary and the posting file are deleted ");

    }

//    private static void deleteDirectory(String filePath) throws IOException {
//        try {
//            File file  = new File(filePath);
//            if(file.isDirectory()){
//                String[] childFiles = file.list();
//                if(childFiles == null) {
//                    //Directory is empty. Proceed for deletion
//                    file.delete();
//                }
//                else {
//                    //Directory has other files.
//                    //Need to delete them first
//                    for (String childFilePath :  childFiles) {
//                        //recursive delete the files
//                        deleteDirectory(childFilePath);
//                    }
//                }
//
//            }
//            else {
//                //it is a simple file. Proceed for deletion
//                file.delete();
//            }
//        }
//
//        catch (Exception e)
//        {
//
//        }
//    }

    public long dicNumTerms(){
        long n=0;
        indexer.getDicNumTerms();
        return n;
    }

    public void finishData()
    {//present all of the Data that is needed aout the program
        AlertBox.display("Program Information",
                "time of running:"+totalTime+"\n"+
                        "number of files indexed: "+numOfDocIndex+"\n"+
                        "number of unique terms: "+indexer.getDicNumTerms()+"\n"+
                "number of all terms in corpus: "+indexer.dicNumTerms);

    }

}