package jb.game.suguru;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.security.keystore.StrongBoxUnavailableException;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Jan on 9-2-2019.
 */

class Data extends SQLiteOpenHelper {
    private static Data mInstance = null;

    private static final String cDBName = "Suguru.db";
    private static final int cDBVersion = 2;
    private static String mExternalFilesDir;

    static Data getInstance(Context pContext) {
        Context lContext;
        File lExternalFilesDir;
        /*
         * use the application context as suggested by CommonsWare.
         * this will ensure that you dont accidentally leak an Activitys
         * context (see this article for more information:
         * http://developer.android.com/resources/articles/avoiding-memory-leaks.html)
         */
        if (mInstance == null) {
            lContext = pContext.getApplicationContext();
            lExternalFilesDir = lContext.getExternalFilesDir(null);
            if (lExternalFilesDir == null) {
                mExternalFilesDir = "";
            } else {
                mExternalFilesDir = lExternalFilesDir.getAbsolutePath();
            }
            mInstance = new Data(lContext);
        }
        return mInstance;
    }

    /**
     * constructor should be private to prevent direct instantiation.
     * make call to static factory method "getInstance()" instead.
     */
    private Data(Context pContext) {
        super(pContext, mExternalFilesDir + "/" + cDBName, null, cDBVersion);
    }

    @Override
    public synchronized void close() {
        super.close();
        mInstance = null;
    }

    @Override
    public void onCreate(SQLiteDatabase pDB) {
        sDefineGame(pDB);
        sInitGame(pDB);
        sDefineGroup(pDB);
        sDefinePlayField(pDB);
        sDefineCell(pDB);
        sDefineLib(pDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase pDB, int pOldVersion, int pNewVersion) {
        switch (pOldVersion){
            case 1:
                sDefineLib(pDB);
                break;
            default:
                pDB.execSQL("DROP TABLE IF EXISTS SuguruGame");
                pDB.execSQL("DROP TABLE IF EXISTS PlayGroup");
                pDB.execSQL("DROP TABLE IF EXISTS PlayField");
                pDB.execSQL("DROP TABLE IF EXISTS Cell");
                pDB.execSQL("DROP TABLE IF EXISTS Lib");
                onCreate(pDB);
                break;
        }
    }

    private void sDefineGame(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE SuguruGame " +
                        "(ContextId Text primary key, " +
                        "Rows Integer Not Null, " +
                        "Columns Integer Not Null, " +
                        "MaxValue Integer Not Null, " +
                        "Status Integer Not Null, " +
                        "Difficulty Integer Not Null, " +
                        "Lib Integer Not Null, " +
                        "SelectedField Integer Not Null, " +
                        "UsedTime Integer Not Null" +
                        ")"
        );
    }

    private void sInitGame(SQLiteDatabase pDB) {
        pDB.execSQL(
                "INSERT INTO SuguruGame " +
                        "(ContextId, Rows, Columns, MaxValue, Status, Difficulty, Lib, SelectedField, UsedTime) " +
                        "VALUES " +
                        "('Suguru', 5, 5, 5, 0, 0, 0, -1, 0)"
        );
    }

    private void sDefineGroup(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE PlayGroup " +
                        "(GroupId Integer primary key, " +
                        "Content Text Not Null" +
                        ")"
        );
    }

    private void sDefinePlayField(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE PlayField " +
                        "(FieldId Integer primary key, " +
                        "Selection Integer Not Null, " +
                        "Pencil Integer Not Null" +
                        ")"
        );
    }

    private void sDefineCell(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE Cell " +
                        "(_ID Integer primary key, " +
                        "FieldId Integer Not Null, " +
                        "CellNumber Integer Not Null, " +
                        "Value Integer Not Null, " +
                        "Fixed Integer Not Null, " +
                        "Confl Integer Not Null, " +
                        "SetupSel Integer Not Null, " +
                        "SetupTaken Integer Not Null, " +
                        "Pencil Text Not Null" +
                        ")"
        );
    }

    private void sDefineLib(SQLiteDatabase pDB) {
        pDB.execSQL(
                "CREATE TABLE Lib " +
                        "(_ID Integer primary key, " +
                        "BatchId Integer Not Null, " +
                        "GameId Integer Not Null, " +
                        "Rows Integer Not Null, " +
                        "Columns Integer Not Null, " +
                        "Difficulty Integer Not Null, " +
                        "Content Text Not Null" +
                        ")"
        );
    }

    void xSaveGame(SuguruGame pGame) {
        SQLiteDatabase lDB;
        List<PlayField> lFields;

        lDB = this.getWritableDatabase();

        sUpdateGame(lDB, pGame);
        sSaveGroups(lDB, pGame);
        sSavePlayField(lDB, pGame.xPlayField());

        lDB.close();
    }

    private void sSavePlayField(SQLiteDatabase pDB, PlayField pPlayField) {
        Cell[] lCells;
        int lCount;

        sDeletePlayField(pDB, pPlayField.xFieldId());
        sNewPlayField(pDB, pPlayField);
        lCells = pPlayField.xCells();
        for (lCount = 0; lCount < lCells.length; lCount++) {
            sNewCell(pDB, pPlayField.xFieldId(), lCount, lCells[lCount]);
        }
    }

    private void sDeletePlayField(SQLiteDatabase pDB, int pPlayFieldId) {
        String lSelection;
        String[] lSelectionArgs;

        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pPlayFieldId);

        pDB.delete("PlayField", lSelection, lSelectionArgs);

        pDB.delete("Cell", lSelection, lSelectionArgs);
    }

    private void sNewPlayField(SQLiteDatabase pDB, PlayField pField) {
        ContentValues lValues;

        lValues = new ContentValues();
        lValues.put("FieldId", pField.xFieldId());
        lValues.put("Selection", pField.xSelection());
        lValues.put("Pencil", (pField.xPencilMode()) ? 1 : 0);

        pDB.insert("PlayField", null, lValues);
    }

    private void sNewCell(SQLiteDatabase pDB, int pFieldId, int pCellNumber, Cell pCell) {
        ContentValues lValues;

        lValues = new ContentValues();
        lValues.put("FieldId", pFieldId);
        lValues.put("CellNumber", pCellNumber);
        lValues.put("Value", pCell.xValue());
        lValues.put("Fixed", (pCell.xFixed()) ? 1 : 0);
        lValues.put("Confl", (pCell.xConflict()) ? 1 : 0);
        lValues.put("SetupSel", (pCell.xSetupSel()) ? 1 : 0);
        lValues.put("SetupTaken", (pCell.xSetupTaken()) ? 1 : 0);
        lValues.put("Pencil", pCell.xPencils());

        pDB.insert("Cell", null, lValues);
    }

    void xDeleteSave() {
        SQLiteDatabase lDB;

        lDB = this.getWritableDatabase();

        lDB.delete("PlayGroup", null, null);
        lDB.delete("PlayField", null, null);
        lDB.delete("Cell", null, null);

        lDB.close();
    }

    private void sUpdateGame(SQLiteDatabase pDB, SuguruGame pGame) {
        ContentValues lValues;
        String lSelection;
        String[] lSelectionArgs;

        lValues = new ContentValues();
        lValues.put("Rows", pGame.xRows());
        lValues.put("Columns", pGame.xColumns());
        lValues.put("MaxValue", pGame.xMaxValue());
        lValues.put("Status", pGame.xGameStatus());
        lValues.put("Difficulty", 0);
        lValues.put("Lib", (pGame.xLib()) ? 1 : 0);
        lValues.put("SelectedField", 0);
        lValues.put("UsedTime", pGame.xUsedTime());
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[]{"Suguru"};

        pDB.update("SuguruGame", lValues, lSelection, lSelectionArgs);
    }

    private void sSaveGroups(SQLiteDatabase pDB, SuguruGame pGame) {
        ContentValues lValues;
        List<Group> lGroups;
        Group lGroup;
        int lCount;

        pDB.delete("PlayGroup", null, null);

        lGroups = pGame.xGroups();
        for (lCount = 0; lCount < lGroups.size(); lCount++) {
            lGroup = lGroups.get(lCount);

            lValues = new ContentValues();
            lValues.put("GroupId", lCount);
            lValues.put("Content", lGroup.xContent());

            pDB.insert("PlayGroup", null, lValues);
        }
    }

    SuguruGame xCurrentGame() {
        SuguruGame lGame;
        List<Group> lGroups;
        List<PlayField> lFields;
        SQLiteDatabase lDB;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        int lGameRows = 0;
        int lGameColumns = 0;
        int lMaxValue = 0;
        int lStatus = 0;
        int lDifficulty = -1;
        int lLib = 0;
        int lSelectedField = 0;
        int lUsedTime = 0;

        lDB = this.getReadableDatabase();
        lColumns = new String[]{"Rows", "Columns", "MaxValue", "Status", "Difficulty", "Lib", "SelectedField", "UsedTime"};
        lSelection = "ContextId = ?";
        lSelectionArgs = new String[]{"Suguru"};

        try {
            lCursor = lDB.query("SuguruGame", lColumns, lSelection, lSelectionArgs, null, null, null);
            if (lCursor.moveToNext()) {
                lGameRows = lCursor.getInt(0);
                lGameColumns = lCursor.getInt(1);
                lMaxValue = lCursor.getInt(2);
                lStatus = lCursor.getInt(3);
                lDifficulty = lCursor.getInt(4);
                lLib = lCursor.getInt(5);
                lSelectedField = lCursor.getInt(6);
                lUsedTime = lCursor.getInt(7);
            }
            lCursor.close();
        } catch (Exception ignored) { }

        lGroups = sGetGroups(lDB);
        lFields = sGetFields(lDB, lGameRows * lGameColumns);

        lDB.close();

        lGame = new SuguruGame(lGroups, lFields, lGameRows, lGameColumns, lMaxValue, lStatus, lDifficulty, (lLib == 0) ? false : true, lSelectedField, lUsedTime);
        return lGame;
    }

    private List<Group> sGetGroups(SQLiteDatabase pDB) {
        List<Group> lGroups;
        Group lGroup;
        Cursor lCursor;
        String[] lColumns;
        String lSequence;
        int lGroupId;
        String lContent;
        int lPencil;


        lGroups = new ArrayList<>();
        lColumns = new String[]{"GroupId", "Content"};
        lSequence = "GroupId";

        lCursor = pDB.query("PlayGroup", lColumns, null, null, null, null, lSequence);
        while (lCursor.moveToNext()) {
            lGroupId = lCursor.getInt(0);
            lContent = lCursor.getString(1);
            lGroup = new Group(lContent);
            lGroups.add(lGroup);
        }
        lCursor.close();
        return lGroups;
    }

    private List<PlayField> sGetFields(SQLiteDatabase pDB, int pSize) {
        List<PlayField> lFields;
        PlayField lField;
        Cell[] lCells;
        Cursor lCursor;
        String[] lColumns;
        String lSequence;
        int lFieldId;
        int lSel;
        int lPencil;

        lFields = new ArrayList<>();

        lColumns = new String[]{"FieldId", "Selection", "Pencil"};
        lSequence = "FieldId";

        lCursor = pDB.query("PlayField", lColumns, null, null, null, null, lSequence);
        while (lCursor.moveToNext()) {
            lFieldId = lCursor.getInt(0);
            lSel = lCursor.getInt(1);
            lPencil = lCursor.getInt(2);
            lCells = sGetCells(pDB, lFieldId, pSize);
            //noinspection RedundantConditionalExpression
            lField = new PlayField(lFieldId, lCells, lSel, (lPencil == 0) ? false : true);
            lFields.add(lField);
        }
        lCursor.close();

        return lFields;
    }

    private Cell[] sGetCells(SQLiteDatabase pDB, int pFieldId, int pSize) {
        Cell[] lCells;
        Cell lCell;
        Cursor lCursor;
        String[] lColumns;
        String lSelection;
        String[] lSelectionArgs;
        String lSequence;
        int lCellNumber;
        int lValue;
        int lFixed;
        int lConflict;
        int lSetupSel;
        int lSetupTaken;
        String lPencil;

        lColumns = new String[]{"CellNumber", "Value", "Fixed", "Confl", "SetupSel", "SetupTaken", "Pencil"};
        lSelection = "FieldId = ?";
        lSelectionArgs = new String[1];
        lSelectionArgs[0] = String.valueOf(pFieldId);
        lSequence = "CellNumber";

        lCells = new Cell[pSize];

        lCursor = pDB.query("Cell", lColumns, lSelection, lSelectionArgs, null, null, lSequence);
        while (lCursor.moveToNext()) {
            lCellNumber = lCursor.getInt(0);
            lValue = lCursor.getInt(1);
            lFixed = lCursor.getInt(2);
            lConflict = lCursor.getInt(3);
            lSetupSel = lCursor.getInt(4);
            lSetupTaken = lCursor.getInt(5);
            lPencil = lCursor.getString(6);
            //noinspection RedundantConditionalExpression
            lCell = new Cell(lValue, (lFixed == 0) ? false : true, (lConflict == 0) ? false : true, (lSetupSel == 0) ? false : true, (lSetupTaken == 0) ? false : true, lPencil);
            lCells[lCellNumber] = lCell;
        }
        lCursor.close();

        return lCells;
    }

    void xLibGame(SuguruGame pGame, int pDifficulty){
        SQLiteDatabase lDB;
        int lGameId;
        ContentValues lValues;

        lDB = this.getWritableDatabase();
        lGameId = sMaxGameId(lDB);
        lGameId++;


        lValues = new ContentValues();
        lValues.put("BatchId", 0);
        lValues.put("GameId", lGameId);
        lValues.put("Rows", pGame.xRows());
        lValues.put("Columns", pGame.xColumns());
        lValues.put("Difficulty", pDifficulty);
        lValues.put("Content", pGame.xGameBasic());

        lDB.insert("Lib", null, lValues);

        lDB.close();
    }

    private int sMaxGameId(SQLiteDatabase pDB){
        Cursor lCursor;
        int lMax;

        lCursor = pDB.rawQuery("SELECT MAX(GameId) FROM Lib WHERE BatchId = '0'", null);
        if (lCursor.moveToFirst()){
            lMax = lCursor.getInt(0);
        } else {
            lMax = -1;
        }
        lCursor.close();
        return lMax;
    }

    LibGame xRandomLibGame(int pDifficulty){
        SQLiteDatabase lDB;
        Cursor lCursor;
        int lSize;
        Random lRandom;
        int lPos;
        String[] lColumns;
        LibGame lLibGame = null;

        lDB = this.getReadableDatabase();

        lColumns = new String[]{"Rows", "Columns", "Content"};

        lCursor = lDB.query("Lib", lColumns, null, null, null, null, null);

        lSize = lCursor.getCount();
        if (lSize > 0){
            lRandom = new Random();
            lPos = lRandom.nextInt(lSize);
            lCursor.moveToPosition(lPos);

            lLibGame = new LibGame();
            lLibGame.xRows = lCursor.getInt(0);
            lLibGame.xColumns = lCursor.getInt(1);
            lLibGame.xContent = lCursor.getString(2);
        }
        lCursor.close();

        lDB.close();

        return lLibGame;
    }
}