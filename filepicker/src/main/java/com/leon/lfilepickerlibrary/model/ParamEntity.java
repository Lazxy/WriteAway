package com.leon.lfilepickerlibrary.model;

import java.io.Serializable;

/**
 * 作者：Leon
 * 时间：2017/3/21 14:50
 */
public class ParamEntity implements Serializable {
    /**
     * 起始目录路径
     */
    private String startPath;
    /**
     * 需要选择的文件类型
     */
    private String[] fileTypes;
    /**
     * Toolbar标题
     */
    private String pickerTitle;
    /**
     * Toolbar标题字体风格
     */
    private int titleStyle;
    /**
     * 选择器整体风格
     */
    private int pickerTheme;
    /**
     * Toolbar返回按钮样式
     */
    private int backIcon;
    /**
     * 表示文件的图标
     */
    private int fileIcon;
    /**
     * 表示文件夹的图标
     */
    private int folderIcon;
    /**
     * 是否为选择文件模式 false表示只选择文件夹
     */
    private boolean chooseFileMode;
    /**
     * 是否为多选模式
     */
    private boolean multiMode;
    /**
     * 多选模式下选择确认按钮的文案
     */
    private String multiModeConfirmText;
    /**
     * 未选择到文件/文件夹的提示
     */
    private String notFoundTips;
    /**
     * 是否选择大于标准文件大小的文件 false为选择小于标准文件大小的文件
     */
    private boolean isGreater;
    /**
     * 标准文件大小
     */
    private long standardFileSize;
    /**
     * 最大文件选择数量
     */
    private int maxNum;

    public String getStartPath() {
        return startPath;
    }

    public void setStartPath(String startPath) {
        this.startPath = startPath;
    }

    public String[] getFileTypes() {
        return fileTypes;
    }

    public void setFileTypes(String[] fileTypes) {
        this.fileTypes = fileTypes;
    }

    public String getPickerTitle() {
        return pickerTitle;
    }

    public void setPickerTitle(String pickerTitle) {
        this.pickerTitle = pickerTitle;
    }

    public int getTitleStyle() {
        return titleStyle;
    }

    public void setTitleStyle(int titleStyle) {
        this.titleStyle = titleStyle;
    }

    public int getPickerTheme() {
        return pickerTheme;
    }

    public void setPickerTheme(int pickerTheme) {
        this.pickerTheme = pickerTheme;
    }

    public int getBackIcon() {
        return backIcon;
    }

    public void setBackIcon(int backIcon) {
        this.backIcon = backIcon;
    }

    public int getFileIcon() {
        return fileIcon;
    }

    public void setFileIcon(int fileIcon) {
        this.fileIcon = fileIcon;
    }

    public int getFolderIcon() {
        return folderIcon;
    }

    public void setFolderIcon(int folderIcon) {
        this.folderIcon = folderIcon;
    }

    public boolean isChooseFileMode() {
        return chooseFileMode;
    }

    public void setChooseFileMode(boolean chooseFileMode) {
        this.chooseFileMode = chooseFileMode;
    }

    public boolean isMultiMode() {
        return multiMode;
    }

    public void setMultiMode(boolean multiMode) {
        this.multiMode = multiMode;
    }

    public String getMultiModeConfirmText() {
        return multiModeConfirmText;
    }

    public void setMultiModeConfirmText(String multiModeConfirmText) {
        this.multiModeConfirmText = multiModeConfirmText;
    }

    public String getNotFoundTips() {
        return notFoundTips;
    }

    public void setNotFoundTips(String notFoundTips) {
        this.notFoundTips = notFoundTips;
    }

    public boolean isGreater() {
        return isGreater;
    }

    public void setGreater(boolean greater) {
        isGreater = greater;
    }

    public long getStandardFileSize() {
        return standardFileSize;
    }

    public void setStandardFileSize(long standardFileSize) {
        this.standardFileSize = standardFileSize;
    }

    public int getMaxNum() {
        return maxNum;
    }

    public void setMaxNum(int maxNum) {
        this.maxNum = maxNum;
    }
}
