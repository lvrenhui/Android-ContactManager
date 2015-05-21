package com.uc.contactmanager.common.tool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.util.LruCache;
import cn.ninegame.gamemanager.app.state.EnvironmentState;
import cn.ninegame.gamemanager.config.SharePrefConstant;
import cn.ninegame.gamemanager.model.parcel.recommend.RecommendKeywordInfo;
import cn.ninegame.gamemanager.module.log.L;
import cn.ninegame.gamemanager.module.message.Message;
import cn.ninegame.gamemanager.module.message.MessagePump;
import cn.ninegame.gamemanager.net.operation.GetSearchAdKeywordsOperation;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author zhonglong zhonglong@ucweb.com
 * @Description: 用户客户端一些本地数据的缓存
 * @date 2015/3/27 14:47
 */
public class LocalDataManager {

    private volatile static LocalDataManager sLocalDataManager = null;

    /**
     * 游戏攻略助手点击
     */
    private JSONObject mStrategiesClickedList;

    /**
     * 游戏角标礼包点击
     */
    private JSONObject mGiftClickedList;

    /**
     * 搜索热词
     */
    private ArrayList<RecommendKeywordInfo> mDefaultKeywords = new ArrayList<RecommendKeywordInfo>();

    private LruCache<String, String> mSessionCacheMap;

    public static LocalDataManager getInstance() {
        if (sLocalDataManager == null) {
            synchronized (LocalDataManager.class) {
                if (sLocalDataManager == null) {
                    sLocalDataManager = new LocalDataManager();
                }
            }
        }
        return sLocalDataManager;
    }

    private LocalDataManager() {
        mSessionCacheMap = new LruCache<String, String>(1024 * 200) {   // 200KB
            @Override
            protected int sizeOf(String key, String value) {

                return value.length() * 2; // in java, char encoding is UTF16_BE, always 2 bytes
            }
        };
    }

    /**
     *获取StrategiesClicked列表
     */
    public JSONObject getStrategiesClickedList() {
        if (mStrategiesClickedList == null) {
            String strategiesStr = EnvironmentState.getInstance().getPreferences().getString(SharePrefConstant.PREFS_KEY_STRATEGIES_CLICKED_LIST, "{}");
            try {
                mStrategiesClickedList = new JSONObject(strategiesStr);
            } catch (Exception e) {
                L.w(e);
            }
        }
        return mStrategiesClickedList;
    }

    /**
     * 移除StrategiesClicked
     * @param gameId
     */
    public void removedStrategiesClicked(String gameId) {
        mStrategiesClickedList = getStrategiesClickedList();
        if (mStrategiesClickedList.has(gameId)) {
            mStrategiesClickedList.remove(gameId);
            EnvironmentState.getInstance().getPreferences().edit().putString(SharePrefConstant.PREFS_KEY_STRATEGIES_CLICKED_LIST, mStrategiesClickedList.toString()).commit();
        }
    }

    /**
     * 更新StrategiesClicked列表
     * @param strategiesObj
     */
    public void updateStrategiesClickedList(JSONObject strategiesObj) {
        if (strategiesObj != null && strategiesObj.length() > 0) {
            mStrategiesClickedList = strategiesObj;
            EnvironmentState.getInstance().getPreferences().edit().putString(SharePrefConstant.PREFS_KEY_STRATEGIES_CLICKED_LIST, strategiesObj.toString()).commit();
        }
    }

    /**
     * 解析搜索推荐词数据
     *
     * @param bundle
     * @return
     */
    private ArrayList<RecommendKeywordInfo> parseDefaultKeywords(Bundle bundle) {
        ArrayList<RecommendKeywordInfo> infos;
        bundle.setClassLoader(RecommendKeywordInfo.class.getClassLoader());
        infos = bundle.getParcelableArrayList(GetSearchAdKeywordsOperation.BUNDLE_INPUT_KEYWORDS_DATA);
        return infos;
    }

    /**
     * 获取搜索关键词
     *
     * @return
     */
    @Nullable
    public ArrayList<RecommendKeywordInfo> getSearchRecommendKeyword() {
        return mDefaultKeywords;
    }

    /**
     * 设置搜索关键词
     *
     * @param recommendKeywords
     */
    public void setSearchRecommendKeyword(ArrayList<RecommendKeywordInfo> recommendKeywords) {
        if (recommendKeywords != null && recommendKeywords.size() > 0) {
            mDefaultKeywords = recommendKeywords;
        }
    }

    /**
     * 保存搜索热词，并广播热
     * @param resultData
     */
    public void cacheSearchRecommendKeyword(Bundle resultData) {
//        if (mDefaultKeywords != null) {
//            mDefaultKeywords.clear();
//        }
        mDefaultKeywords = parseDefaultKeywords(resultData);
        MessagePump.getInstance().broadcastMessage(Message.Type.REFRESH_CATE_KEY_WORD, mDefaultKeywords, Message.PRIORITY_EXTREMELY_HIGH);
    }

    /**
     * 获取GiftClicked列表
     */
    public JSONObject getGiftClickedList() {
        if (mGiftClickedList == null) {
            String giftStr = EnvironmentState.getInstance().getPreferences().getString(SharePrefConstant.PREFS_KEY_GIFT_CLICKED_LIST, "{}");
            try {
                mGiftClickedList = new JSONObject(giftStr);
            } catch (Exception e) {
                L.w(e);
            }
        }
        return mGiftClickedList;
    }

    /**
     * 移除GiftClicked
     * @param gameId
     */
    public void removedGiftClicked(String gameId) {
        mGiftClickedList = getGiftClickedList();
        if (mGiftClickedList.has(gameId)) {
            mGiftClickedList.remove(gameId);
            EnvironmentState.getInstance().getPreferences().edit().putString(SharePrefConstant.PREFS_KEY_GIFT_CLICKED_LIST, mGiftClickedList.toString()).commit();
        }
    }

    /**
     * 更新GiftClicked
     * @param giftObj
     */
    public void updateGiftClickedList(JSONObject giftObj) {
        if (giftObj != null && giftObj.length() > 0) {
            mGiftClickedList = giftObj;
            EnvironmentState.getInstance().getPreferences().edit().putString(SharePrefConstant.PREFS_KEY_GIFT_CLICKED_LIST, giftObj.toString()).commit();
        }
    }

    public String getSession(String key, String defValue) {
        String value = mSessionCacheMap.get(key);
        return value == null ? defValue : value;
    }

    public void removeSession(String key) {
        mSessionCacheMap.remove(key);
    }

    public void setSession(String key, String value) {
        mSessionCacheMap.put(key, value);
    }


}
