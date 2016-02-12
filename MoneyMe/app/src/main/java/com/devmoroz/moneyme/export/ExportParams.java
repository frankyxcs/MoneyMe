package com.devmoroz.moneyme.export;


import java.sql.Timestamp;

public class ExportParams {

    public enum ExportTarget {SD_CARD, SHARING, DROPBOX}

    private Timestamp mExportStartTime = Timestamp.valueOf(Exporter.TIMESTAMP_ZERO);

    private ExportTarget mExportTarget= ExportTarget.SHARING;

    public Timestamp getExportStartTime(){
        return mExportStartTime;
    }

    public void setExportStartTime(Timestamp exportStartTime){
        this.mExportStartTime = exportStartTime;
    }

    public ExportTarget getExportTarget() {
        return mExportTarget;
    }

    public void setExportTarget(ExportTarget mExportTarget) {
        this.mExportTarget = mExportTarget;
    }

}
