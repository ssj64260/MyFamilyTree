package com.cxb.myfamilytree.widget.familytree;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.cxb.myfamilytree.R;
import com.cxb.myfamilytree.model.FamilyBean;
import com.cxb.myfamilytree.utils.DisplayUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.cxb.myfamilytree.model.FamilyBean.SEX_FEMALE;
import static com.cxb.myfamilytree.model.FamilyBean.SEX_MALE;

/**
 * 家谱树自定义ViewGroup（没有有养父母）
 */

public class FamilyTreeView extends ViewGroup {

    private static final int SPACE_WIDTH_DP = 20;//间距为20dp
    private static final int ITEM_WIDTH_DP = 50;//家庭成员View宽度50dp
    private static final int ITEM_HEIGHT_DP = 80;//家庭成员View高度80dp
    private static final float CALL_TEXT_SIZE_SP = 9f;//称呼文字大小9sp
    private static final float NAME_TEXT_SIZE_SP = 11f;//名称文字大小11sp
    private static final int LINE_WIDTH_DP = 1;//连线宽度2dp
    private static final int SCROLL_WIDTH = 2;//移动超过2dp，响应滑动，否则属于点击

    private static final int BACKGROUND_NORMAL = R.drawable.shape_bg_normal;//默认背景
    private static final int BACKGROUND_SELEDTED = R.drawable.shape_bg_select;//选中项背景
    private static final int AVATAR_MALE = R.drawable.ic_avatar_male;//男性默认头像
    private static final int AVATAR_FEMALE = R.drawable.ic_avatar_female;//女性默认头像

    private OnFamilyClickListener mOnFamilyClickListener;

    private int mItemWidthPX;//家庭成员View宽度PX
    private int mItemHeightPX;//家庭成员View高度PX
    private int mSpacePX;//元素间距PX
    private int mLineWidthPX;//连线宽度PX

    private int[] mGenerationTop;//每代顶部位置
    private int[] mGenerationLeft;//每代左边位置
    private int[] mGenerationRight;//每代右边位置

    private int mWidthMeasureSpec;
    private int mHeightMeasureSpec;

    private int mShowWidthPX;//在屏幕所占的宽度
    private int mShowHeightPX;//在屏幕所占的高度

    private int mScrollWidth;//移动范围
    private int mCurrentX;//当前X轴偏移量
    private int mCurrentY;//当前Y轴偏移量
    private int mLastTouchX;//最后一次触摸的X坐标
    private int mLastTouchY;//最后一次触摸的Y坐标
    private int mLastInterceptX;
    private int mLastInterceptY;

    private int mCurrentLeft;//当前选中View离左边距离
    private int mCurrentTop;//当前选中View离顶部距离

    private Paint mPaint;//连线样式
    private Path mPath;//路径

    private FamilyBean mMyInfo;//我
    private FamilyBean mMyParentInfo;//父母
    private FamilyBean mMyPGrandParentInfo;//爷爷，奶奶
    private FamilyBean mMyMGrandParentInfo;//外公，外婆
    private List<FamilyBean> mMyChildrenInfo;//子女
    private List<FamilyBean> mMyLittleBrotherInfo;//弟弟，妹妹
    private List<FamilyBean> mMyBrotherInfo;//哥哥，姐姐
    private List<FamilyBean> mMyFaUncleInfo;//叔伯姑
    private List<FamilyBean> mMyMoUncleInfo;//舅姨

    private View mSelectView;//当前选中View
    private View mOnlyMyView;//我View
    private Pair<View, View> mMyView;//我和配偶View
    private Pair<View, View> mMyParentView;//我的父母View
    private Pair<View, View> mMyPGrandParentView;//我的爷爷奶奶View
    private Pair<View, View> mMyMGrandParentView;//我的外公外婆View

    private List<Pair<View, View>> mMyChildrenView;//我的子女View
    private List<Pair<View, View>> mMyGrandChildrenView;//我的子孙View

    private List<Pair<View, View>> mMyLittleBrotherView;//我的亲弟弟妹妹View
    private List<Pair<View, View>> mMyLittleBroChildrenView;//我的亲弟弟妹妹的子女View
    private List<Pair<View, View>> mMyBrotherView;//我的亲哥哥姐姐View
    private List<Pair<View, View>> mMyBroChildrenView;//我的亲哥哥姐姐的子女View

    private List<Pair<View, View>> mMyMoUncleView;//我的舅姨View
    private List<Pair<View, View>> mMyMoUncleChildrenView;//我的舅姨的子女View
    private List<Pair<View, View>> mMyFaUncleView;//我的叔伯姑View
    private List<Pair<View, View>> mMyFaUncleChildrenView;//我的叔伯姑的子女View

    private FamilyDBHelper mDBHelper;

    public FamilyTreeView(Context context) {
        this(context, null, 0);
    }

    public FamilyTreeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FamilyTreeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FamilyTreeView);
        final int textColor = typedArray.getColor(R.styleable.FamilyTreeView_ftv_line_color, 0xFFBBBBBB);
        typedArray.recycle();

        mScrollWidth = DisplayUtil.dip2px(SCROLL_WIDTH);
        mSpacePX = DisplayUtil.dip2px(SPACE_WIDTH_DP);
        mLineWidthPX = DisplayUtil.dip2px(LINE_WIDTH_DP);
        mItemWidthPX = DisplayUtil.dip2px(ITEM_WIDTH_DP);
        mItemHeightPX = DisplayUtil.dip2px(ITEM_HEIGHT_DP);
        mWidthMeasureSpec = MeasureSpec.makeMeasureSpec(mItemWidthPX, MeasureSpec.EXACTLY);
        mHeightMeasureSpec = MeasureSpec.makeMeasureSpec(mItemHeightPX, MeasureSpec.EXACTLY);

        mGenerationTop = new int[5];
        mGenerationTop[0] = 0;
        mGenerationTop[1] = mGenerationTop[0] + mItemHeightPX + mSpacePX;
        mGenerationTop[2] = mGenerationTop[1] + mItemHeightPX + mSpacePX;
        mGenerationTop[3] = mGenerationTop[2] + mItemHeightPX + mSpacePX;
        mGenerationTop[4] = mGenerationTop[3] + mItemHeightPX + mSpacePX;
        mGenerationLeft = new int[5];
        mGenerationRight = new int[5];

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.reset();
        mPaint.setColor(textColor);
        mPaint.setStrokeWidth(mLineWidthPX);
        mPaint.setStyle(Paint.Style.STROKE);

        mPath = new Path();
        mPath.reset();

        mDBHelper = new FamilyDBHelper(context);

        mMyChildrenInfo = new ArrayList<>();
        mMyBrotherInfo = new ArrayList<>();
        mMyLittleBrotherInfo = new ArrayList<>();
        mMyFaUncleInfo = new ArrayList<>();
        mMyMoUncleInfo = new ArrayList<>();

        mMyChildrenView = new ArrayList<>();
        mMyGrandChildrenView = new ArrayList<>();
        mMyLittleBrotherView = new ArrayList<>();
        mMyLittleBroChildrenView = new ArrayList<>();
        mMyBrotherView = new ArrayList<>();
        mMyBroChildrenView = new ArrayList<>();
        mMyMoUncleView = new ArrayList<>();
        mMyMoUncleChildrenView = new ArrayList<>();
        mMyFaUncleView = new ArrayList<>();
        mMyFaUncleChildrenView = new ArrayList<>();
    }

    public void drawFamilyTree(FamilyBean family, boolean isToCenter) {
        recycleAllView();
        initData(family);
        initView();
        invalidate();

        if (isToCenter) {
            scrollToCenter();
        }
    }

    public void destroyView() {
        recycleAllView();
        if (mDBHelper != null) {
            mDBHelper.closeDB();
        }
    }

    private void recycleAllView() {
        removeAllViews();

        for (int i = 0; i < mGenerationLeft.length; i++) {
            mGenerationLeft[i] = 0;
        }
        for (int i = 0; i < mGenerationRight.length; i++) {
            mGenerationRight[i] = 0;
        }

        mMyInfo = null;
        mMyParentInfo = null;
        mMyPGrandParentInfo = null;
        mMyMGrandParentInfo = null;
        mMyChildrenInfo.clear();
        mMyBrotherInfo.clear();
        mMyLittleBrotherInfo.clear();
        mMyFaUncleInfo.clear();
        mMyMoUncleInfo.clear();

        mMyView = null;
        mOnlyMyView = null;
        mMyParentView = null;
        mMyPGrandParentView = null;
        mMyMGrandParentView = null;
        mMyChildrenView.clear();
        mMyGrandChildrenView.clear();
        mMyLittleBrotherView.clear();
        mMyLittleBroChildrenView.clear();
        mMyBrotherView.clear();
        mMyBroChildrenView.clear();
        mMyMoUncleView.clear();
        mMyMoUncleChildrenView.clear();
        mMyFaUncleView.clear();
        mMyFaUncleChildrenView.clear();
    }

    private void initData(FamilyBean family) {
        mMyInfo = family;
        if (mMyInfo != null) {
            mMyInfo.setSelect(true);
            mDBHelper.setSpouse(mMyInfo);

            mMyChildrenInfo.addAll(mDBHelper.getChildrenAndGrandChildren(mMyInfo, ""));

            final String fatherId = mMyInfo.getFatherId();
            final String motherId = mMyInfo.getMotherId();
            mMyParentInfo = mDBHelper.getCouple(fatherId, motherId);
            if (mMyParentInfo != null) {
                final FamilyBean father;
                final FamilyBean mother;
                if (SEX_MALE.equals(mMyParentInfo.getSex())) {
                    father = mMyParentInfo;
                    mother = mMyParentInfo.getSpouse();
                } else {
                    father = mMyParentInfo.getSpouse();
                    mother = mMyParentInfo;
                }

                if (father != null) {
                    final String pGrandFatherId = father.getFatherId();
                    final String pGrandMotherId = father.getMotherId();
                    mMyPGrandParentInfo = mDBHelper.getCouple(pGrandFatherId, pGrandMotherId);
                    if (mMyPGrandParentInfo != null) {
                        mMyFaUncleInfo.addAll(mDBHelper.getChildrenAndGrandChildren(mMyPGrandParentInfo, fatherId));
                    }
                }
                if (mother != null) {
                    final String mGrandFatherId = mother.getFatherId();
                    final String mGrandMotherId = mother.getMotherId();
                    mMyMGrandParentInfo = mDBHelper.getCouple(mGrandFatherId, mGrandMotherId);
                    if (mMyMGrandParentInfo != null) {
                        mMyMoUncleInfo.addAll(mDBHelper.getChildrenAndGrandChildren(mMyMGrandParentInfo, motherId));
                    }
                }
            }

            mMyBrotherInfo.addAll(mDBHelper.getMyBrothers(mMyInfo, false));
            mMyLittleBrotherInfo.addAll(mDBHelper.getMyBrothers(mMyInfo, true));
        }
    }

    private void initView() {
        initEachRightPart(5, mMyChildrenInfo, mMyChildrenView, mMyGrandChildrenView);
        measureMaxCoordinate(4, true, mMyChildrenView);
        measureMaxCoordinate(4, false, mMyChildrenView);

        initMyPart();

        initEachRightPart(4, mMyLittleBrotherInfo, mMyLittleBrotherView, mMyLittleBroChildrenView);
        measureMaxCoordinate(3, true, mMyLittleBrotherView);
        initEachLeftPart(4, mMyBrotherInfo, mMyBrotherView, mMyBroChildrenView);
        measureMaxCoordinate(3, false, mMyBrotherView);

        initMyParentPart();

        initEachRightPart(3, mMyMoUncleInfo, mMyMoUncleView, mMyMoUncleChildrenView);
        measureMaxCoordinate(2, true, mMyMoUncleView);
        initEachLeftPart(3, mMyFaUncleInfo, mMyFaUncleView, mMyFaUncleChildrenView);
        measureMaxCoordinate(2, false, mMyFaUncleView);
    }

    private void initEachRightPart(int generation,
                                   List<FamilyBean> parentinfoList,
                                   List<Pair<View, View>> parentViewList,
                                   List<Pair<View, View>> childrenViewList) {
        final int position = generation - 1;
        final int count = parentinfoList.size();
        final List<FamilyBean> parentinfos = new ArrayList<>(parentinfoList);

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean parentInfo = parentinfos.get(i);
                setRightPart(position, parentInfo, parentViewList, childrenViewList);
            }
        } else {
            mGenerationRight[position] += (mItemWidthPX + mSpacePX);
        }
    }

    private void initEachLeftPart(int generation,
                                  List<FamilyBean> parentinfoList,
                                  List<Pair<View, View>> parentViewList,
                                  List<Pair<View, View>> childrenViewList) {
        final int position = generation - 1;
        final int count = parentinfoList.size();
        final List<FamilyBean> parentinfos = new ArrayList<>(parentinfoList);
        Collections.reverse(parentinfos);
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean parentInfo = parentinfos.get(i);
                setLeftPart(position, parentInfo, parentViewList, childrenViewList);
            }
        } else {
            mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
        }
    }

    private void setRightPart(int position,
                              FamilyBean familyInfo,
                              List<Pair<View, View>> parentViewList,
                              List<Pair<View, View>> childrenViewList) {
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final String familySex = familyInfo.getSex();
        final List<FamilyBean> childList = new ArrayList<>(familyInfo.getChildren());

        final int count = childList.size();
        final int startLeft = mGenerationRight[position];

        if (familySpouseInfo != null && count == 1) {
            final FamilyBean childInfo = childList.get(0);
            final String childSex = childInfo.getSex();
            final FamilyBean childSpouseInfo = childInfo.getSpouse();
            if (childSpouseInfo == null) {
                final int maleLeft = mGenerationRight[position];
                final int femaleLeft = maleLeft + mItemWidthPX + mSpacePX;

                final View maleView;
                final View femaleView;
                if (SEX_MALE.equals(familySex)) {
                    maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position - 1]);
                } else {
                    maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position - 1]);
                }

                final int parentViewPosition = parentViewList.size();
                parentViewList.add(parentViewPosition, Pair.create(maleView, femaleView));

                final int childLeft = (maleLeft + femaleLeft) / 2;
                final View childView = createFamilyView(childInfo, childLeft, mGenerationTop[position]);
                final View childSpouseView = null;
                final int childViewPosition = childrenViewList.size();
                if (SEX_MALE.equals(childSex)) {
                    childrenViewList.add(childViewPosition, Pair.create(childView, childSpouseView));
                } else {
                    childrenViewList.add(childViewPosition, Pair.create(childSpouseView, childView));
                }

                mGenerationRight[position] = femaleLeft + (mItemWidthPX + mSpacePX);
                return;
            }
        }

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean childInfo = childList.get(i);
                final String childSex = childInfo.getSex();
                final FamilyBean childSpouseInfo = childInfo.getSpouse();

                final int maleLeft = mGenerationRight[position];
                mGenerationRight[position] += (mItemWidthPX + mSpacePX);
                final int femaleLeft = mGenerationRight[position];
                final int childPosition = childrenViewList.size();

                final View maleView;
                final View femaleView;
                if (childSpouseInfo != null) {
                    if (SEX_MALE.equals(childSex)) {
                        maleView = createFamilyView(childInfo, maleLeft, mGenerationTop[position]);
                        femaleView = createFamilyView(childSpouseInfo, femaleLeft, mGenerationTop[position]);
                    } else {
                        maleView = createFamilyView(childSpouseInfo, maleLeft, mGenerationTop[position]);
                        femaleView = createFamilyView(childInfo, femaleLeft, mGenerationTop[position]);
                    }

                    mGenerationRight[position] += (mItemWidthPX + mSpacePX);
                } else {
                    if (SEX_MALE.equals(childSex)) {
                        maleView = createFamilyView(childInfo, maleLeft, mGenerationTop[position]);
                        femaleView = null;
                    } else {
                        maleView = null;
                        femaleView = createFamilyView(childInfo, maleLeft, mGenerationTop[position]);
                    }
                }
                childrenViewList.add(childPosition, Pair.create(maleView, femaleView));
            }
        } else {
            mGenerationRight[position] += (mItemWidthPX + mSpacePX);
            if (familySpouseInfo != null) {
                mGenerationRight[position] += (mItemWidthPX + mSpacePX);
            }
        }

        final int parentViewPosition = parentViewList.size();
        final int centerLeft = (startLeft + mGenerationRight[position] - (mItemWidthPX + mSpacePX)) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        parentViewList.add(parentViewPosition, setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, familyInfo));
    }

    private void setLeftPart(int position,
                             FamilyBean familyInfo,
                             List<Pair<View, View>> parentViewList,
                             List<Pair<View, View>> childrenViewList) {
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final String familySex = familyInfo.getSex();
        final List<FamilyBean> childList = new ArrayList<>(familyInfo.getChildren());

        Collections.reverse(childList);

        final int count = childList.size();
        final int startLeft = mGenerationLeft[position];

        if (familySpouseInfo != null && count == 1) {
            final FamilyBean childInfo = childList.get(0);
            final String childSex = childInfo.getSex();
            final FamilyBean childSpouseInfo = childInfo.getSpouse();
            if (childSpouseInfo == null) {
                final int maleLeft = mGenerationLeft[position] - mSpacePX - mItemWidthPX;
                final int femaleLeft = mGenerationLeft[position];
                final View maleView;
                final View femaleView;
                if (SEX_MALE.equals(familySex)) {
                    maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position - 1]);
                } else {
                    maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position - 1]);
                    femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position - 1]);
                }

                final int parentViewPosition = 0;
                parentViewList.add(parentViewPosition, Pair.create(maleView, femaleView));

                final int childLeft = (maleLeft + femaleLeft) / 2;
                final View childView = createFamilyView(childInfo, childLeft, mGenerationTop[position]);
                final View childSpouseView = null;
                final int childViewPosition = 0;
                if (SEX_MALE.equals(childSex)) {
                    childrenViewList.add(childViewPosition, Pair.create(childView, childSpouseView));
                } else {
                    childrenViewList.add(childViewPosition, Pair.create(childSpouseView, childView));
                }

                mGenerationLeft[position] = maleLeft - (mItemWidthPX + mSpacePX);
                return;
            }
        }

        if (count > 0) {
            for (int i = 0; i < count; i++) {
                final FamilyBean childInfo = childList.get(i);
                final String childSex = childInfo.getSex();
                final FamilyBean childSpouseInfo = childInfo.getSpouse();

                final int femaleLeft = mGenerationLeft[position];
                mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                final int maleLeft = mGenerationLeft[position];
                final int childPosition = 0;

                final View maleView;
                final View femaleView;
                if (childSpouseInfo != null) {
                    if (SEX_MALE.equals(childSex)) {
                        maleView = createFamilyView(childInfo, maleLeft, mGenerationTop[position]);
                        femaleView = createFamilyView(childSpouseInfo, femaleLeft, mGenerationTop[position]);
                    } else {
                        maleView = createFamilyView(childSpouseInfo, maleLeft, mGenerationTop[position]);
                        femaleView = createFamilyView(childInfo, femaleLeft, mGenerationTop[position]);
                    }

                    mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
                } else {
                    if (SEX_MALE.equals(childSex)) {
                        maleView = createFamilyView(childInfo, femaleLeft, mGenerationTop[position]);
                        femaleView = null;
                    } else {
                        maleView = null;
                        femaleView = createFamilyView(childInfo, femaleLeft, mGenerationTop[position]);
                    }
                }
                childrenViewList.add(childPosition, Pair.create(maleView, femaleView));
            }
        } else {
            mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
            if (familySpouseInfo != null) {
                mGenerationLeft[position] -= (mItemWidthPX + mSpacePX);
            }
        }

        final int parentViewPosition = 0;
        final int centerLeft = (startLeft + mGenerationLeft[position] + (mItemWidthPX + mSpacePX)) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        parentViewList.add(parentViewPosition, setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, familyInfo));
    }

    private void initMyPart() {
        final int generation = 3;
        final int position = generation - 1;
        final int centerLeft = (mGenerationRight[position + 1] + mGenerationLeft[position + 1]) / 2;
        final int maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
        final int femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
        mMyView = setParentLocate(position, centerLeft, maleLeft, femaleLeft, mMyInfo);
        if (SEX_MALE.equals(mMyInfo.getSex())) {
            mOnlyMyView = mMyView.first;
        } else {
            mOnlyMyView = mMyView.second;
        }

        mSelectView = mOnlyMyView;

        final List<Pair<View, View>> myPairList = new ArrayList<>(1);
        myPairList.add(mMyView);
        measureMaxCoordinate(generation, true, myPairList);
        measureMaxCoordinate(generation, false, myPairList);
        if (mGenerationLeft[position + 1] > mGenerationLeft[position]) {
            mGenerationLeft[position + 1] = mGenerationLeft[position];
        }
        if (mGenerationRight[position + 1] < mGenerationRight[position]) {
            mGenerationRight[position + 1] = mGenerationRight[position];
        }
    }

    private void initMyParentPart() {
        final int generation = 2;
        if (mMyParentInfo != null) {
            final int position = generation - 1;
            final int parentCenterLeft;
            final String mySex = mMyInfo.getSex();
            if (SEX_MALE.equals(mySex)) {
                parentCenterLeft = mMyView.first.getLeft();
            } else {
                parentCenterLeft = mMyView.second.getLeft();
            }
            final int parentMaleLeft = parentCenterLeft - (mItemWidthPX + mSpacePX) / 2;
            final int parentFemaleLeft = parentCenterLeft + (mItemWidthPX + mSpacePX) / 2;
            mMyParentView = setParentLocate(position, parentCenterLeft, parentMaleLeft, parentFemaleLeft, mMyParentInfo);

            final List<Pair<View, View>> myParentPairList = new ArrayList<>(1);
            myParentPairList.add(mMyParentView);
            measureMaxCoordinate(generation, true, myParentPairList);
            measureMaxCoordinate(generation, false, myParentPairList);
            if (mGenerationLeft[position + 1] > mGenerationLeft[position]) {
                mGenerationLeft[position + 1] = mGenerationLeft[position];
            }
            if (mGenerationRight[position + 1] < mGenerationRight[position]) {
                mGenerationRight[position + 1] = mGenerationRight[position];
            }

            final View fatherView = mMyParentView.first;
            final View motherView = mMyParentView.second;
            if (mMyPGrandParentInfo != null) {
                final int centerLeft;
                final int maleLeft;
                final int femaleLeft;
                if (mMyMGrandParentInfo != null) {
                    centerLeft = fatherView.getLeft();
                    maleLeft = centerLeft - (mItemWidthPX + mSpacePX);
                    femaleLeft = centerLeft;
                } else {
                    centerLeft = fatherView.getLeft();
                    maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
                    femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
                }
                mMyPGrandParentView = setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, mMyPGrandParentInfo);
            }
            if (mMyMGrandParentInfo != null) {
                final int centerLeft;
                final int maleLeft;
                final int femaleLeft;
                if (mMyPGrandParentInfo != null) {
                    centerLeft = motherView.getLeft();
                    maleLeft = centerLeft;
                    femaleLeft = centerLeft + (mItemWidthPX + mSpacePX);
                } else {
                    centerLeft = motherView.getLeft();
                    maleLeft = centerLeft - (mItemWidthPX + mSpacePX) / 2;
                    femaleLeft = centerLeft + (mItemWidthPX + mSpacePX) / 2;
                }
                mMyMGrandParentView = setParentLocate(position - 1, centerLeft, maleLeft, femaleLeft, mMyMGrandParentInfo);
            }
        }
    }

    private Pair<View, View> setParentLocate(int position, int centerLeft, int maleLeft, int femaleLeft, FamilyBean familyInfo) {
        final String familySex = familyInfo.getSex();
        final FamilyBean familySpouseInfo = familyInfo.getSpouse();
        final View maleView;
        final View femaleView;
        if (familySpouseInfo != null) {
            if (SEX_MALE.equals(familySex)) {
                maleView = createFamilyView(familyInfo, maleLeft, mGenerationTop[position]);
                femaleView = createFamilyView(familySpouseInfo, femaleLeft, mGenerationTop[position]);
            } else {
                maleView = createFamilyView(familySpouseInfo, maleLeft, mGenerationTop[position]);
                femaleView = createFamilyView(familyInfo, femaleLeft, mGenerationTop[position]);
            }
            return Pair.create(maleView, femaleView);
        } else {
            if (SEX_MALE.equals(familySex)) {
                maleView = createFamilyView(familyInfo, centerLeft, mGenerationTop[position]);
                femaleView = null;
            } else {
                maleView = null;
                femaleView = createFamilyView(familyInfo, centerLeft, mGenerationTop[position]);
            }
            return Pair.create(maleView, femaleView);
        }
    }

    //计量该代最右或最左的坐标
    private void measureMaxCoordinate(int generation, boolean isRight, List<Pair<View, View>> pairList) {
        final int position = generation - 1;
        if (pairList.size() > 0) {
            if (isRight) {
                final Pair<View, View> lastPair = pairList.get(pairList.size() - 1);
                final View lastView = lastPair.second != null ? lastPair.second : lastPair.first;
                mGenerationRight[position] = lastView.getLeft();
                mGenerationRight[position] += mItemWidthPX + mSpacePX;
            } else {
                final Pair<View, View> firstPair = pairList.get(0);
                final View firstView = firstPair.first != null ? firstPair.first : firstPair.second;
                mGenerationLeft[position] = firstView.getLeft();
                mGenerationLeft[position] -= (mSpacePX + mItemWidthPX);
            }
        }
    }

    private View createFamilyView(FamilyBean family, int left, int top) {
        final View familyView = LayoutInflater.from(getContext()).inflate(R.layout.item_family, this, false);
        familyView.setLayoutParams(new LayoutParams(mItemWidthPX, mItemHeightPX));
        familyView.setLeft(left);
        familyView.setTop(top);
        familyView.setTag(family);

        final ImageView ivAvatar = familyView.findViewById(R.id.iv_avatar);
        ivAvatar.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, mItemWidthPX));

        final TextView tvName = familyView.findViewById(R.id.tv_name);
        tvName.setTextSize(NAME_TEXT_SIZE_SP);
        tvName.setText(family.getMemberName());

        final TextView tvCall = familyView.findViewById(R.id.tv_call);
        tvCall.setTextSize(CALL_TEXT_SIZE_SP);
        tvCall.setText("(");
        tvCall.append(family.getCall());
        tvCall.append(")");

        final String url = family.getMemberImg();
        final String sex = family.getSex();
        Glide.with(getContext())
                .load(url)
                .placeholder(SEX_FEMALE.equals(sex) ? AVATAR_FEMALE : AVATAR_MALE)
                .error(SEX_FEMALE.equals(sex) ? AVATAR_FEMALE : AVATAR_MALE)
                .centerCrop()
                .dontAnimate()
                .into(ivAvatar);
        if (family.isSelect()) {
            familyView.setBackgroundResource(BACKGROUND_SELEDTED);
        } else {
            familyView.setBackgroundResource(BACKGROUND_NORMAL);
        }

        familyView.setOnClickListener(mClick);

        this.addView(familyView);
        return familyView;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mShowWidthPX = MeasureSpec.getSize(widthMeasureSpec);
        mShowHeightPX = MeasureSpec.getSize(heightMeasureSpec);

        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            childView.measure(mWidthMeasureSpec, mHeightMeasureSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        final int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            setChildViewFrame(childView, childView.getLeft(), childView.getTop(), mItemWidthPX, mItemHeightPX);
        }

        if (mCurrentLeft == 0 && mCurrentTop == 0) {
            mCurrentLeft = (mShowWidthPX - mItemWidthPX) / 2;
            mCurrentTop = (mShowHeightPX - mItemHeightPX) / 2;
        }

        if (mOnlyMyView != null) {
            scrollTo(mOnlyMyView.getLeft() - mCurrentLeft, mOnlyMyView.getTop() - mCurrentTop);
        }
    }

    private void setChildViewFrame(View childView, int left, int top, int width, int height) {
        childView.layout(left, top, left + width, top + height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
        // 画选中项的儿子和其子女相关连线
        drawPart(canvas, mMyChildrenInfo, mMyChildrenView, mMyGrandChildrenView);
        // 画选中项的弟弟妹妹和其子女相关连线
        drawPart(canvas, mMyLittleBrotherInfo, mMyLittleBrotherView, mMyLittleBroChildrenView);
        // 画选中项的哥哥姐姐和其子女相关连线
        drawPart(canvas, mMyBrotherInfo, mMyBrotherView, mMyBroChildrenView);
        // 画选中项母亲的兄弟姐妹和其子女相关连线
        drawPart(canvas, mMyMoUncleInfo, mMyMoUncleView, mMyMoUncleChildrenView);
        // 画选中项父亲的兄弟界面和其子女相关连线
        drawPart(canvas, mMyFaUncleInfo, mMyFaUncleView, mMyFaUncleChildrenView);

        // 画选中项相关连线
        final int mMyX = drawIndependentViewLine(canvas, mMyInfo, mMyView, 3);
        // 画选中项的父母相关连线
        final int mParentX = drawIndependentViewLine(canvas, mMyParentInfo, mMyParentView, 2);
        // 画选中项的祖父母相关连线
        final int mPGrandParentX = drawIndependentViewLine(canvas, mMyPGrandParentInfo, mMyPGrandParentView, 1);
        // 画选中项的外祖父母相关连线
        final int mMGrandParentX = drawIndependentViewLine(canvas, mMyMGrandParentInfo, mMyMGrandParentView, 1);

        //
        drawOtherLine(canvas, mMyChildrenInfo, mMyChildrenView, mMyX);
        drawOtherLine(canvas, mMyLittleBrotherInfo, mMyLittleBrotherView, mParentX);
        drawOtherLine(canvas, mMyBrotherInfo, mMyBrotherView, mParentX);
        if (mMyParentView != null) {
            final View fatherView = mMyParentView.first;
            if (fatherView != null && mMyPGrandParentView != null) {
                final int horizontalY = fatherView.getTop() - mSpacePX / 2;
                final int horizontalStartX = fatherView.getLeft() + mItemWidthPX / 2;
                drawLine(canvas, horizontalStartX, mPGrandParentX, horizontalY, horizontalY);
            }
            final View motherView = mMyParentView.second;
            if (motherView != null && mMyMGrandParentView != null) {
                final int horizontalY = motherView.getTop() - mSpacePX / 2;
                final int horizontalStartX = motherView.getLeft() + mItemWidthPX / 2;
                drawLine(canvas, horizontalStartX, mMGrandParentX, horizontalY, horizontalY);
            }
        }
        drawOtherLine(canvas, mMyFaUncleInfo, mMyFaUncleView, mPGrandParentX);
        drawOtherLine(canvas, mMyMoUncleInfo, mMyMoUncleView, mMGrandParentX);
    }

    /**
     *
     */
    private void drawPart(Canvas canvas,
                          List<FamilyBean> parentInfoList,
                          List<Pair<View, View>> parentViewList,
                          List<Pair<View, View>> childrenViewList) {
        int childIndex = 0;
        final int parentCount = parentInfoList.size();
        for (int i = 0; i < parentCount; i++) {
            final FamilyBean parentInfo = parentInfoList.get(i);
            final Pair<View, View> parentPair = parentViewList.get(i);
            final List<FamilyBean> childrenInfoList = parentInfo.getChildren();
            final int childrenCount = childrenInfoList.size();
            final int centerX = drawViewLine(canvas, parentInfo, parentPair);

            for (int j = 0; j < childrenCount; j++, childIndex++) {
                final FamilyBean childInfo = childrenInfoList.get(j);
                final Pair<View, View> childPair = childrenViewList.get(childIndex);
                drawViewLine(canvas, childInfo, childPair);

                if (j == 0 || j == childrenCount - 1) {
                    final String childSex = childInfo.getSex();
                    final View childView;
                    if (SEX_MALE.equals(childSex)) {
                        childView = childPair.first;
                    } else {
                        childView = childPair.second;
                    }
                    final int horizontalY = childView.getTop() - mSpacePX / 2;
                    final int horizontalStartX = childView.getLeft() + mItemWidthPX / 2;
                    drawLine(canvas, horizontalStartX, centerX, horizontalY, horizontalY);
                }
            }
        }
    }

    /**
     * 画夫妻双方相关线。（仅画通用部分）
     * 1、夫妻双方连线。
     * 2、夫妻双方连线中点到子女的连线。（连线只画与子女连线的一半，剩下一半由子女往上画）
     * 3、夫妻亲属方与其父母的连线。（假设女方为亲属方）
     * 4、返回夫妻双方子女的连线的中点X坐标。
     *                    |
     *                    |(3)
     *                    |
     *  ------         ------
     *  |    |   (1)   |    |
     *  |    |---------|    |
     *  |    |    |    |    |
     *  ------    |(2) ------
     *            |←--------------（4、返回这个点的X坐标）
     *            .
     *            .
     *            .
     *         ------
     *         |    |
     *         |    |
     *         |    |
     *         ------
     */
    private int drawViewLine(Canvas canvas, FamilyBean familyInfo, Pair<View, View> familyPair) {
        int centerX = 0;
        final String familySex = familyInfo.getSex();
        final String fatherId = familyInfo.getFatherId();
        final String motherId = familyInfo.getMotherId();
        final List<FamilyBean> childrenList = familyInfo.getChildren();
        final View familyView;
        final View familySpouseView;
        if (SEX_MALE.equals(familySex)) {
            familyView = familyPair.first;
            familySpouseView = familyPair.second;
        } else {
            familyView = familyPair.second;
            familySpouseView = familyPair.first;
        }
        //夫妻双方都存在
        if (familyView != null && familySpouseView != null) {
            final int horizontalStartX = familyView.getLeft() + mItemWidthPX / 2;
            final int horizontalEndX = familySpouseView.getLeft() + mItemWidthPX / 2;
            final int horizontalY = familyView.getTop() + mItemHeightPX / 2;

            // 画夫妻之间连线
            drawLine(canvas, horizontalStartX, horizontalEndX, horizontalY, horizontalY);
            if (childrenList != null && childrenList.size() > 0) {
                final int verticalX = (horizontalEndX + horizontalStartX) / 2;
                final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                // 画夫妻间连线中点到子女的连线
                drawLine(canvas, verticalX, verticalX, horizontalY, vertiaclEndY);
                centerX = verticalX;
            }
        } else {
            if (familyView != null && childrenList != null && childrenList.size() > 0) {
                final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                final int verticalStartY = familyView.getTop() + mItemHeightPX / 2;
                final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                drawLine(canvas, verticalX, verticalX, verticalStartY, vertiaclEndY);
                centerX = verticalX;
            }
        }

        // 画夫妻亲属方与其父母的连线
        if (familyView != null && (!TextUtils.isEmpty(fatherId) || !TextUtils.isEmpty(motherId))) {
            final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
            final int verticalStartY = familyView.getTop();
            final int verticalEndY = verticalStartY - mSpacePX / 2;
            drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
        }

        // 返回夫妻双方子女的连线的中点坐标
        return centerX;
    }

    /**
     * 画特殊部分连线，根据传入的参数决定四个模块中的哪一个。（包括：1、选中项；2、选中项父母；3、选中项祖父母；4、选中项外祖父母）
     * 1、夫妻双方连线。
     * 2、夫妻双方连线中点到子女的连线。
     * 3、夫妻双方其父母的连线。
     * 4、返回夫妻双方子女的连线的中点X坐标。
     *
     * 【1、选中项。（假设女方为选中项）】
     *                    |
     *                    |(3)
     *                    |
     *  ------         ------
     *  |    |   (1)   ||||||
     *  |    |---------||||||
     *  |    |    |    ||||||
     *  ------    |(2) ------
     *            |←--------------（4、返回这个点的X坐标）
     *            .
     *            .
     *            .
     *         ------
     *         |    |
     *         |    |
     *         |    |
     *         ------
     *
     * 【2、选中项父母】
     *     |              |
     *     |(3)           |(3)
     *     |              |
     *  ------         ------
     *  |    |   (1)   |    |
     *  | 父 |---------| 母 |
     *  |    |    |    |    |
     *  ------    |(2) ------
     *            |←--------------（4、返回这个点的X坐标）
     *            .
     *            .
     *            .
     *         ------
     *         ||||||
     *         ||||||
     *         ||||||
     *         ------
     *
     * 【3、选中项祖父母；4、选中项外祖父母】
     *  ------         ------
     *  |    |   (1)   |    |
     *  |    |---------|    |
     *  |    |    |    |    |
     *  ------    |(2) ------
     *            |←--------------（4、返回这个点的X坐标）
     *            .
     *            .
     *            .
     *         ------
     *         |    |
     *         |父/母|
     *         |    |
     *         ------
     */
    private int drawIndependentViewLine(Canvas canvas, FamilyBean familyInfo, Pair<View, View> familyPair, int generation) {
        int centerX = 0;
        if (familyInfo != null) {
            final boolean haveTopLine;//是否有与父母的连线
            final boolean haveSpoueTopLine;//配偶是否有与父母的连线
            final boolean haveBottomLine;//是否有与子女的连线

            //一代选中项与配偶都没有父母连线，其他如此类推
            if (generation == 1) {
                haveTopLine = false;
                haveSpoueTopLine = false;
                haveBottomLine = true;
            } else if (generation == 2) {
                haveTopLine = true;
                haveSpoueTopLine = true;
                haveBottomLine = true;
            } else if (generation == 3) {//三代为选中项
                haveTopLine = true;
                haveSpoueTopLine = false;
                haveBottomLine = mMyChildrenInfo.size() > 0;
            } else {
                return centerX;
            }
            final String familySex = familyInfo.getSex();
            final String fatherId = familyInfo.getFatherId();
            final String motherId = familyInfo.getMotherId();
            final FamilyBean spouseInfo = familyInfo.getSpouse();
            final View familyView;
            final View familySpouseView;
            if (SEX_MALE.equals(familySex)) {
                familyView = familyPair.first;
                familySpouseView = familyPair.second;
            } else {
                familyView = familyPair.second;
                familySpouseView = familyPair.first;
            }

            //夫妻双方都存在
            if (familyView != null && familySpouseView != null) {
                final int horizontalStartX = familyView.getLeft() + mItemWidthPX / 2;
                final int horizontalEndX = familySpouseView.getLeft() + mItemWidthPX / 2;
                final int horizontalY = familyView.getTop() + mItemHeightPX / 2;

                //画夫妻双方连线
                drawLine(canvas, horizontalStartX, horizontalEndX, horizontalY, horizontalY);
                if (haveBottomLine) {
                    final int verticalX = (horizontalEndX + horizontalStartX) / 2;
                    final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;

                    //画夫妻双方连线中点到子女的连线
                    drawLine(canvas, verticalX, verticalX, horizontalY, vertiaclEndY);
                    centerX = verticalX;
                }
            } else {
                if (haveBottomLine && familyView != null) {
                    final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                    final int verticalStartY = familyView.getTop() + mItemHeightPX / 2;
                    final int vertiaclEndY = familyView.getTop() + mItemHeightPX + mSpacePX / 2;
                    drawLine(canvas, verticalX, verticalX, verticalStartY, vertiaclEndY);
                    centerX = verticalX;
                }
            }

            if (haveTopLine) {
                //画夫妻亲属方与其父母的连线
                if (familyView != null && (!TextUtils.isEmpty(fatherId) || !TextUtils.isEmpty(motherId))) {
                    final int verticalX = familyView.getLeft() + mItemWidthPX / 2;
                    final int verticalStartY = familyView.getTop();
                    final int verticalEndY = verticalStartY - mSpacePX / 2;
                    drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
                }

                //画配偶与其父母的连线
                if (haveSpoueTopLine && spouseInfo != null && familySpouseView != null) {
                    final String spouseFatherId = spouseInfo.getFatherId();
                    final String spouseMotherId = spouseInfo.getMotherId();
                    if (!TextUtils.isEmpty(spouseFatherId) || !TextUtils.isEmpty(spouseMotherId)) {
                        final int verticalX = familySpouseView.getLeft() + mItemWidthPX / 2;
                        final int verticalStartY = familySpouseView.getTop();
                        final int verticalEndY = verticalStartY - mSpacePX / 2;
                        drawLine(canvas, verticalX, verticalX, verticalStartY, verticalEndY);
                    }
                }
            }
        }
        return centerX;
    }

    /**
     *
     */
    private void drawOtherLine(Canvas canvas, List<FamilyBean> familyInfoList, List<Pair<View, View>> pairList, int centerX) {
        final int count = pairList.size();
        if (count > 0) {
            final Pair<View, View> firstPair = pairList.get(0);
            final Pair<View, View> lastPair = pairList.get(count - 1);
            final FamilyBean firstInfo = familyInfoList.get(0);
            final FamilyBean lastInfo = familyInfoList.get(count - 1);
            final String firstSex = firstInfo.getSex();
            final String lastSex = lastInfo.getSex();
            final View firstView;
            final View lastView;
            if (SEX_MALE.equals(firstSex)) {
                firstView = firstPair.first;
            } else {
                firstView = firstPair.second;
            }
            if (SEX_MALE.equals(lastSex)) {
                lastView = lastPair.first;
            } else {
                lastView = lastPair.second;
            }
            final int firstX = firstView.getLeft() + mItemWidthPX / 2;
            final int lastX = lastView.getLeft() + mItemWidthPX / 2;
            final int horizontalY = firstView.getTop() - mSpacePX / 2;

            if (firstX <= centerX) {
                if (lastX >= centerX) {
                    drawLine(canvas, firstX, centerX, horizontalY, horizontalY);
                    drawLine(canvas, lastX, centerX, horizontalY, horizontalY);
                } else {
                    drawLine(canvas, firstX, lastX, horizontalY, horizontalY);
                    drawLine(canvas, lastX, centerX, horizontalY, horizontalY);
                }
            } else {
                if (lastX >= centerX) {
                    drawLine(canvas, firstX, centerX, horizontalY, horizontalY);
                    drawLine(canvas, lastX, firstX, horizontalY, horizontalY);
                }
            }
        }
    }

    private void drawLine(Canvas canvas, int startX, int endX, int startY, int endY) {
        mPath.reset();
        mPath.moveTo(startX, startY);
        mPath.lineTo(endX, endY);
        canvas.drawPath(mPath, mPaint);
    }

    public void setOnFamilyClickListener(OnFamilyClickListener onFamilyClickListener) {
        this.mOnFamilyClickListener = onFamilyClickListener;
    }

    public void setShowBottomSpouse(boolean showBottomSpouse) {
        mDBHelper.setInquirySpouse(showBottomSpouse);
    }

    public boolean isShowBottomSpouse() {
        return mDBHelper.ismInquirySpouse();
    }

    public void scrollToCenter() {
        if (mSelectView != null) {
            mCurrentLeft = (mShowWidthPX - mItemWidthPX) / 2;
            mCurrentTop = (mShowHeightPX - mItemHeightPX) / 2;
            scrollTo(mSelectView.getLeft() - mCurrentLeft, mSelectView.getTop() - mCurrentTop);
        }
    }

    private OnClickListener mClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mSelectView = v;
            if (mOnFamilyClickListener != null) {
                final FamilyBean family = (FamilyBean) v.getTag();
                mCurrentLeft = v.getLeft() - getScrollX();
                mCurrentTop = v.getTop() - getScrollY();
                mOnFamilyClickListener.onFamilySelect(v, family);
            }
        }
    };

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                final int currentTouchX = (int) event.getX();
                final int currentTouchY = (int) event.getY();

                final int distanceX = currentTouchX - mLastTouchX;
                final int distanceY = currentTouchY - mLastTouchY;

                mCurrentX -= distanceX;
                mCurrentY -= distanceY;

                mCurrentLeft += distanceX;
                mCurrentTop += distanceY;

                this.scrollTo(mCurrentX, mCurrentY);
                mLastTouchX = currentTouchX;
                mLastTouchY = currentTouchY;
                break;
            case MotionEvent.ACTION_UP:
                performClick();
                break;
        }
        return true;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        boolean intercerpt = false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastInterceptX = (int) event.getX();
                mLastInterceptY = (int) event.getY();
                mCurrentX = getScrollX();
                mCurrentY = getScrollY();
                mLastTouchX = (int) event.getX();
                mLastTouchY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final int distanceX = Math.abs((int) event.getX() - mLastInterceptX);
                final int distanceY = Math.abs((int) event.getY() - mLastInterceptY);
                intercerpt = distanceX >= mScrollWidth || distanceY >= mScrollWidth;
                break;
        }
        return intercerpt;
    }
}
