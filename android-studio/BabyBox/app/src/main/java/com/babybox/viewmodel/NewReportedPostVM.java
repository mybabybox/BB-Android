package com.babybox.viewmodel;

import com.babybox.util.ViewUtil;

import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedString;

public class NewReportedPostVM {
    public Long id;
    public Long postId;
    public Long reporterId;
    public String body;
    public String note;
    public String reportedType;

    public NewReportedPostVM(){}

    public NewReportedPostVM(Long id, String note) {
        this(id, -1L, -1L, null, note, null);
    }

    public NewReportedPostVM(Long postId, Long reporterId, String body, ViewUtil.ReportedType reportedType) {
        this(-1L, postId, reporterId, body, null, reportedType);
    }

    public NewReportedPostVM(Long id, Long postId, Long reporterId, String body, String note, ViewUtil.ReportedType reportedType) {
        this.id = id;
        this.postId = postId;
        this.reporterId = reporterId;
        this.body = body;
        this.note = note;
        this.reportedType = reportedType.name();
    }

    public MultipartTypedOutput toMultipart() {
        MultipartTypedOutput multipartTypedOutput = new MultipartTypedOutput();
        multipartTypedOutput.addPart("id", new TypedString(id+""));
        multipartTypedOutput.addPart("postId", new TypedString(postId+""));
        multipartTypedOutput.addPart("reporterId", new TypedString(reporterId+""));
        multipartTypedOutput.addPart("body", new TypedString(body));
        multipartTypedOutput.addPart("note", new TypedString(note));
        multipartTypedOutput.addPart("reportedType", new TypedString(reportedType));
        return multipartTypedOutput;
    }
}
