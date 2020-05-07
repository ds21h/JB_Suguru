package jb.game.suguru;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SuguruView extends View {
    private final int cMargin = 10;
    private final int cButtonMargin = 20;

    private Paint mPaint = new Paint();

    private final int cColorBackNorm = Color.argb(255, 255, 255, 245);
    private final int cColorBackFixed = Color.argb(255, 230, 230, 230);
    private final int cColorForeNorm = Color.BLACK;
    private final int cColorForeConflict = Color.RED;

    private final int cAlphaNorm = 255;
    private final int cAlphaDisabled = 100;

    private final int cStrokeNone = 0;
    private final int cStrokeNarrow = 3;
    private final int cStrokeSelection = 12;

    private SuguruGame mGame = null;
    private float mCellSize;
    private float mButtonSize;
    private boolean mEnabled;

    public SuguruView(Context pContext) {
        super(pContext);
    }

    public SuguruView(Context pContext, AttributeSet pAttrSet) {
        super(pContext, pAttrSet);
    }

    public void setGame(SuguruGame pGame) {
        mGame = pGame;
    }

    @Override
    public void onDraw(Canvas pCanvas) {
        super.onDraw(pCanvas);

        if (mGame != null){
            mCellSize = (getWidth() - (2 * cMargin)) / (float)mGame.xColumns();
            mButtonSize = (getWidth() / 7F) - (2 * cButtonMargin);
            sDrawPlayField(pCanvas);
            sDrawButtons(pCanvas);
        }
    }

    private void sDrawPlayField(Canvas pCanvas) {
        int lRow;
        int lColumn;
        int lPencilRow;
        int lPencilColumn;
        int lPencilPos;
        float lRowMargin;
        float lColumnMargin;
        RectF lRect;
        PlayCell lCell;
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
                lColumnMargin = (lColumn * mCellSize) + cMargin;
                lCell = mGame.xPlayField().xCell(lRow, lColumn);
                lRect = new RectF(lColumnMargin, lRowMargin, lColumnMargin + mCellSize, lRowMargin + mCellSize);

                //      Background
                mPaint.setStrokeWidth(cStrokeNone);
                mPaint.setStyle(Paint.Style.FILL);
                if (lCell.xFixed()) {
                    mPaint.setColor(cColorBackFixed);
                } else {
                    mPaint.setColor(cColorBackNorm);
                }
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRect, mPaint);

                //      Cell outline
                mPaint.setStrokeWidth(cStrokeNarrow);
                mPaint.setStyle(Paint.Style.STROKE);
                mPaint.setColor(cColorForeNorm);
                mPaint.setAlpha(lAlpha);
                pCanvas.drawRect(lRect, mPaint);

                //      Cell value
                mPaint.setStyle(Paint.Style.FILL);
                if (lCell.xValue() > 0) {
                    //  Normal
                    mPaint.setTextSize(mCellSize * 0.8F);
                    if (lCell.xConflict()) {
                        mPaint.setColor(cColorForeConflict);
                    } else {
                        mPaint.setColor(cColorForeNorm);
                    }
                    mPaint.setAlpha(lAlpha);
                    pCanvas.drawText(String.valueOf(lCell.xValue()), lRect.centerX(), lRect.bottom - mPaint.getFontMetrics().descent, mPaint);
                } else {
                    // Pencil
                    mPaint.setTextSize(lPencilCellSize);
                    mPaint.setColor(cColorForeNorm);
                    mPaint.setAlpha(lAlpha);
                    for (lPencilRow = 0; lPencilRow < 3; lPencilRow++) {
                        for (lPencilColumn = 0; lPencilColumn < 3; lPencilColumn++) {
                            lPencilPos = (lPencilRow * 3) + lPencilColumn;
                            lPencil = lPencilPos / 2;
                            if (lPencilPos == (lPencil * 2)){
                                lPencil++;
                                if (lCell.xPencil(lPencil)) {
                                    pCanvas.drawText(String.valueOf(lPencil), lRect.left + (lPencilColumn * lPencilCellSize) + (lPencilCellSize / 2), lRect.top + ((lPencilRow + 1) * lPencilCellSize) - (mPaint.getFontMetrics().descent / 2), mPaint);
                                }
                            }
                        }
                    }
                }
            }
        }

        mPaint.setStrokeWidth(cStrokeSelection);
        pCanvas.drawRect((mGame.xPlayField().xSelectionColumn() * mCellSize) + cMargin, (mGame.xPlayField().xSelectionRow() * mCellSize) + cMargin, ((mGame.xPlayField().xSelectionColumn() + 1) * mCellSize) + cMargin, ((mGame.xPlayField().xSelectionRow() + 1) * mCellSize) + cMargin, mPaint);
    }

    private void sDrawButtons(Canvas pCanvas) {
/*        RectF lRectF;
        int lCount;
        float lCountSize;
        int lAlpha;

        if (mEnabled){
            lAlpha = cAlphaNorm;
        } else {
            lAlpha = cAlphaDisabled;
        }

        lCountSize = mCellSize / 3;
        for (lCount = 1; lCount <= 9; lCount++) {
            lRectF = sRectButton(lCount);
            mButton[lCount] = lRectF;
            mPaint.setStrokeWidth(cStrokeNone);
            mPaint.setStyle(Paint.Style.FILL);
            if (mGame.xPlayField().xDigitCount(lCount) < 9) {
                mPaint.setColor(cColorButtonNorm);
            } else {
                mPaint.setColor(cColorButtonFull);
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
            pCanvas.drawText(String.valueOf(lCount), lRectF.centerX(), lRectF.bottom - mPaint.getFontMetrics().descent, mPaint);

            mPaint.setTextSize(lCountSize);
            pCanvas.drawText(String.valueOf(mGame.xPlayField().xDigitCount(lCount)), lRectF.right - (lCountSize / 2), lRectF.top + lCountSize, mPaint);
        }
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
}
