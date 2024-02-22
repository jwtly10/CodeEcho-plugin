package jwtly10.codeecho.model;

public class RecordModel {
    TranscriptResponse trans;
    byte[] audio;

    public RecordModel(TranscriptResponse res, byte[] audio) {
        this.trans = res;
        this.audio = audio;
    }

    public TranscriptResponse getTrans() {
        return trans;
    }

    public byte[] getAudio() {
        return audio;
    }

    public void setTrans(TranscriptResponse res) {
        this.trans = res;
    }

    public void setAudio(byte[] audio) {
        this.audio = audio;
    }
}
