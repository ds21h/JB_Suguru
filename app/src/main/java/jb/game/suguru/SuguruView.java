package jb.game.suguru;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class SuguruView extends View {
    interface intSuguruView {
        void onSolved();
    }

    private final int cMargin = 10;
    private final int cButtonMargin = 20;
    private int mMargin = cMargin;

    private final Paint mPaint = new Paint();

    private final int cColorBackNorm = Color.argb(255, 255, 255, 245);
    private final int cColorBackFixed = Color.argb(255, 230, 230, 230);
    private final int cColorBackSel = Color.argb(255, 255, 255, 0);
    private final int cColorBackTaken = Color.argb(255, 200, 200, 200);
    private final int cColorBackConflict = Color.argb(255, 255, 200, 200);
    private final int cColorForeNorm = Color.BLACK;
    private final int cColorForeConflict = Color.RED;
    private final int cColorButtonNorm = Color.argb(255, 255, 255, 245);
    private final int cColorButtonPencil = Color.argb(255, 200, 255, 200);

    private final int cAlphaNorm = 255;
    private final int cAlphaDisabled = 100;

    private final int cStrokeNone = 0;
    private final int cStrokeNarrow = 3;
    private final int cStrokeWide = 10;
    private final int cStrokeSelection = 12;

    private SuguruGame mGame = null;
    private float mCellSize;
    private float mButtonSize;
    private RectF[] mButton = new RectF[6];
    private boolean mEnabled = true;
    private boolean mButtonsEnabled = false;
    private final Context mContext;

    private intSuguruView mIntView = null;

    public SuguruView(Context pContext) {
        super(pContext);
        mContext = pContext;
    }

    public SuguruView(Context pContext, AttributeSet pAttrSet) {
        super(pContext, pAttrSet);
        mContext = pContext;
    }

    public void setGame(SuguruGame pGame) {
        mGame = pGame;
    }

    public void setIntSuguruView(intSuguruView pIntView) {
        mIntView = pIntView;
    }

    @Override
    public boolean onTouchEvent(MotionEvent pEvent) {
        float lDispl;
        int lColumn;
        int lRow;
        boolean lCellSelect;
        RectF lRect;
        int lCount;

        if (mGame != null && mEnabled){
            if (pEvent.getAction() == MotionEvent.ACTION_DOWN && mCellSize > 0) {
                lCellSelect = false;
                lDispl = pEvent.getX() - mMargin;
                if (lDispl >= 0) {
                    lColumn = (int) (lDispl / mCellSize);
                    if (lColumn < mGame.xColumns()) {
                        lDispl = pEvent.getY() - cMargin;
                        if (lDispl >= 0) {
                            lRow = (int) (lDispl / mCellSize);
                            if (lRow < mGame.xRows()) {
                                lCellSelect = true;
                                mGame.xSelection(lRow, lColumn);
                                invalidate();
                            }
                        }
                    }
                }
                if (!lCellSelect) {
                    if (mButtonsEnabled){
                        lRect = new RectF(pEvent.getX(), pEvent.getY(), pEvent.getX(), pEvent.getY());
                        if (mButton[0].contains(lRect)) {
                            if (mGame.xGameStatus() == SuguruGame.cStatusPlay){
                                mGame.xPencilFlip();
                                invalidate();
                            } else {
                                if (mGame.xGameStatus() == SuguruGame.cStatusSetupGroups){
                                    mGame.xStoreGroup();
                                    invalidate();
                                }
                            }
                        } else {
                            if (mGame.xGameStatus() != SuguruGame.cStatusSetupGroups){
                                for (lCount = 1; lCount < mButton.length; lCount++) {
                                    if (mButton[lCount].contains(lRect)) {
                                        mGame.xProcessDigit(lCount);
                                        if (mGame.xGameStatus() == SuguruGame.cStatusSolved) {
                                            if (mIntView != null) {
                                                mIntView.onSolved();
                                            }
                                        }
                                        invalidate();
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);
        float lCellHor;
        float lCellVert;

        if (mGame != null){
            mButtonSize = (getWidth() / (float)(mGame.xMaxValue() + 2)) - (2 * cButtonMargin);
            lCellHor = (getWidth() - (2 * cMargin)) / (float)mGame.xColumns();
            lCellVert = (getHeight() - cMargin - (3 * cButtonMargin) - mButtonSize) / (mGame.xRows() + 0.5F);
            if (lCellHor < lCellVert){
                mCellSize = lCellHor;
                mMargin = cMargin;
            } else {
                mCellSize = lCellVert;
                mMargin = (int)((getWidth() - (mGame.xColumns() * mCellSize)) / 2);
            }
            sDrawPlayField(pCanvas);
            if (mGame.xGameStatus() == SuguruGame.cStatusPlay
                    || mGame.xGameStatus() == SuguruGame.cStatusSetupGroups
                    || mGame.xGameStatus() == SuguruGame.cStatusSetupValues){
                if (mButton.length != mGame.xMaxValue() + 1){
                    mButton = new RectF[mGame.xMaxValue() + 1];
                }
                sDrawButtons(pCanvas);
                mButtonsEnabled = true;
            } else {
                mButtonsEnabled = false;
            }
        }
    }

    private void sDrawPlayField(Canvas pCanvas) {
        int lRow;
        int lColumn;
        int lPencilRow;
        int lPencilColumn;
        float lRowMargin;
        float lColumnMargin;
        RectF lRect;
        ValueCell lValueCell;
        GameCell lGameCell;
        float lPencilCellSize;
        int lPencil;
        int lAlpha;

        if (mEnabled){
            lAlpha = cAlphaNorm;
        } else {
            lAlpha = cAlphaDisabled;
        }
        lPencilCellSize = mCellSize / 3;
        for (lRow = 0; lRow < mGame.xRows(); lRow++) {
            lRowMargin = (lRow * mCellSize) + cMargin;
            for (lColumn = 0; lColumn < mGame.xColumns(); lColumn++) {
                lColumnMargin = (lColumn * mCellSize) + mMargin;
                lValueCell = mGame.xPlayCell(lRow, lColumn);
                lGameCell = mGame.xGameCell(lRow, lColumn);
                lRect = new RectF(lColumnMargin, lRowMargin,
                        lColumnMargin + mCellSize, lRowMargin + mCellSize);

                //      Background
                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                if (mGame.xGameStatus() == SuguruGame.cStatusSetupGroups){
                    if (lGameCell.xSetupTaken()){
//                        if (lValueCell.xSetupTaken()){
                        mPaint.setColor(cColorBackTaken);
                    } else {
                        if (lValueCell.xConflict()){
                            mPaint.setColor(cColorBackConflict);
                        } else {
                            if (lGameCell.xSetupSel()){
//                                if (lValueCell.xSetupSel()){
                                mPaint.setColor(cColorBackSel);
                            } else {
                                mPaint.setColor(cColorBackNorm);
                            }
                        }
                    }
                } else {
                    if (lValueCell.xFixed()) {
                        mPaint.setColor(cColorBackFixed);
                    } else {
                        if (mGame.xIsSelection(lRow, lColumn)){
                            mPaint.setColor(cColorBackSel);
                        } else {
                            mPaint.setColor(cColorBackNorm);
                        }
                    }
                }
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRect, mPaint);

                //      Cell value
                mPaint.setStyle(Paint.Style.FILL);
                if (lValueCell.xValue() > 0) {
                    //  Normal
                    mPaint.setTextSize(mCellSize * 0.8F);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    if (lValueCell.xConflict()) {
                        mPaint.setColor(cColorForeConflict);
                    } else {
                        mPaint.setColor(cColorForeNorm);
                    }
                    mPaint.setAlpha(lAlpha);
                    pCanvas.drawText(String.valueOf(lValueCell.xValue()),
                            lRect.centerX(),
                            lRect.bottom - mPaint.getFontMetrics().descent,
                            mPaint);
                } else {
                    // Pencil
                    mPaint.setTextSize(lPencilCellSize);
                    mPaint.setTextAlign(Paint.Align.CENTER);
                    mPaint.setColor(cColorForeNorm);
                    mPaint.setAlpha(lAlpha);
                    for (lPencilRow = 0; lPencilRow < 3; lPencilRow++) {
                        for (lPencilColumn = 0; lPencilColumn < 3; lPencilColumn++) {
                            lPencil = (lPencilRow * 3) + lPencilColumn + 1;
                            if (lValueCell.xPencil(lPencil)) {
                                pCanvas.drawText(String.valueOf(lPencil), lRect.left + (lPencilColumn * lPencilCellSize) + (lPencilCellSize / 2), lRect.top + ((lPencilRow + 1) * lPencilCellSize) - (mPaint.getFontMetrics().descent / 2), mPaint);
                            }
                        }
                    }
                }

                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(cColorForeNorm);
                mPaint.setAlpha(lAlpha);
                if (lGameCell.xBndLeft()){
                    mPaint.setStrokeWidth(cStrokeWide);
                } else {
                    mPaint.setStrokeWidth(cStrokeNarrow);
                }
                pCanvas.drawLine(lColumnMargin, lRowMargin, lColumnMargin, lRowMargin + mCellSize, mPaint);

                if (lGameCell.xBndRight()){
                    mPaint.setStrokeWidth(cStrokeWide);
                } else {
                    mPaint.setStrokeWidth(cStrokeNarrow);
                }
                pCanvas.drawLine(lColumnMargin + mCellSize, lRowMargin, lColumnMargin + mCellSize, lRowMargin + mCellSize, mPaint);

                if (lGameCell.xBndTop()){
                    mPaint.setStrokeWidth(cStrokeWide);
                } else {
                    mPaint.setStrokeWidth(cStrokeNarrow);
                }
                pCanvas.drawLine(lColumnMargin, lRowMargin, lColumnMargin + mCellSize, lRowMargin, mPaint);

                if (lGameCell.xBndBottom()){
                    mPaint.setStrokeWidth(cStrokeWide);
                } else {
                    mPaint.setStrokeWidth(cStrokeNarrow);
                }
                pCanvas.drawLine(lColumnMargin, lRowMargin + mCellSize, lColumnMargin + mCellSize, lRowMargin + mCellSize, mPaint);
            }
        }
        if (mGame.xFieldCount() > 1){
            mPaint.setStrokeWidth(cStrokeNarrow);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setTextSize(mCellSize * 0.4F);
            mPaint.setTextAlign(Paint.Align.RIGHT);
            mPaint.setColor(cColorForeNorm);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawText(String.valueOf(mGame.xPlayField().xFieldId()), cMargin + (mCellSize * mGame.xColumns()),  (mCellSize * mGame.xRows()) - mPaint.getFontMetrics().top + (cMargin * 2), mPaint);
            mPaint.setTextAlign(Paint.Align.CENTER);
        }
    }

    private void sDrawButtons(Canvas pCanvas) {
        int lAlpha;
        int lCount;
        RectF lRectF;

        if (mEnabled){
            lAlpha = cAlphaNorm;
        } else {
            lAlpha = cAlphaDisabled;
        }
        if (mGame.xGameStatus() == SuguruGame.cStatusSetupGroups){
            if (mGame.xShowGroupButton()){
                lRectF = new RectF();
                lRectF.left = cButtonMargin + mButtonSize;
                lRectF.top = getHeight() - (2 * cButtonMargin) - mButtonSize;
                lRectF.right = getWidth() - cButtonMargin - mButtonSize;
                lRectF.bottom = lRectF.top + mButtonSize;
                mButton[0] = lRectF;

                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(cColorButtonNorm);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setStrokeWidth(cStrokeNarrow);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setTextSize(mButtonSize * 0.8F);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawText(mContext.getString(R.string.vw_group) , lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);
            } else {
                mButtonsEnabled = false;
            }
        } else {
            for (lCount = 1; lCount <= mGame.xMaxValue(); lCount++) {
                lRectF = sRectButton(lCount);
                mButton[lCount] = lRectF;
                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setColor(cColorButtonNorm);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setStrokeWidth(cStrokeNarrow);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setTextSize(mButtonSize * 0.8F);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawText(String.valueOf(lCount), lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);
            }
            lRectF = sRectButton(99);
            mButton[0] = lRectF;
            if (mGame.xGameStatus() != SuguruGame.cStatusSetupValues) {
                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                if (mGame.xPencilMode()) {
                    mPaint.setColor(cColorButtonPencil);
                } else {
                    mPaint.setColor(cColorButtonNorm);
                }
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setStrokeWidth(cStrokeNarrow);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRectF, mPaint);

                mPaint.setColor(cColorForeNorm);
                mPaint.setTextSize(mButtonSize * 0.8F);
                mPaint.setTextAlign(Paint.Align.CENTER);
                mPaint.setStyle(Paint.Style.FILL);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawText(mContext.getString(R.string.vw_pencil) , lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);
            }
        }
/*      float lCountSize;

        lCountSize = mCellSize / 3;
        lRectF = sRectButton(99);
        mButton[0] = lRectF;
        if (!(mGame.xGameStatus() == SudokuGame.cStatusSetup)) {
            mPaint.setStrokeWidth(cStrokeNone);
            mPaint.setStyle(Paint.Style.FILL);
            if (mGame.xPlayField().xPencilMode()) {
                mPaint.setColor(cColorButtonPencil);
            } else {
                mPaint.setColor(cColorButtonNorm);
            }
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setStrokeWidth(cStrokeNarrow);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawRect(lRectF, mPaint);

            mPaint.setColor(cColorForeNorm);
            mPaint.setTextSize(mCellSize);
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setAlpha(lAlpha);
            pCanvas.drawText(mContext.getString(R.string.vw_pencil) , lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);
        } */
    }

    private RectF sRectButton(int pButton) {
        RectF lRect;

        lRect = new RectF();
        if (pButton > 0 && pButton <= mGame.xMaxValue()) {
            lRect.left = cButtonMargin + ((pButton - 1) * (mButtonSize + (2 * cButtonMargin)));
        } else {
            if (pButton == 99) {
                lRect.left = getWidth() - cButtonMargin - mButtonSize;
            } else {
                lRect.left = cButtonMargin;
            }
        }
        lRect.top = getHeight() - (2 * cButtonMargin) - mButtonSize;
        lRect.right = lRect.left + mButtonSize;
        lRect.bottom = lRect.top + mButtonSize;

        return lRect;
    }
}
