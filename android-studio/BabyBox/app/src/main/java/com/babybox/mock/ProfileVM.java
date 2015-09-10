package com.babybox.mock;

public class ProfileVM {
    public long id;
    public String dn;
    public String yr;
    public String gd;
    public String a;
    public long n_p;
    public long n_c;

    // admin readonly fields
    public String n;
    public boolean mb;
    public boolean fb;
    public boolean vl;
    public String em;
    public String sd;
    public String ll;
    public Long tl;
    public Long qc;
    public Long ac;
    public Long lc;

    public boolean following = false;
    public Long numFollowers = 0L;
    public Long numFollowings = 0L;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDn() {
        return dn;
    }

    public void setDn(String dn) {
        this.dn = dn;
    }

    public String getYr() {
        return yr;
    }

    public void setYr(String yr) {
        this.yr = yr;
    }

    public String getGd() {
        return gd;
    }

    public void setGd(String gd) {
        this.gd = gd;
    }

    public String getA() {
        return a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public long getN_p() {
        return n_p;
    }

    public void setN_p(long n_p) {
        this.n_p = n_p;
    }

    public long getN_c() {
        return n_c;
    }

    public void setN_c(long n_c) {
        this.n_c = n_c;
    }

    public String getN() {
        return n;
    }

    public void setN(String n) {
        this.n = n;
    }

    public boolean isMb() {
        return mb;
    }

    public void setMb(boolean mb) {
        this.mb = mb;
    }

    public boolean isFb() {
        return fb;
    }

    public void setFb(boolean fb) {
        this.fb = fb;
    }

    public boolean isVl() {
        return vl;
    }

    public void setVl(boolean vl) {
        this.vl = vl;
    }

    public String getEm() {
        return em;
    }

    public void setEm(String em) {
        this.em = em;
    }

    public String getSd() {
        return sd;
    }

    public void setSd(String sd) {
        this.sd = sd;
    }

    public String getLl() {
        return ll;
    }

    public void setLl(String ll) {
        this.ll = ll;
    }

    public Long getTl() {
        return tl;
    }

    public void setTl(Long tl) {
        this.tl = tl;
    }

    public Long getQc() {
        return qc;
    }

    public void setQc(Long qc) {
        this.qc = qc;
    }

    public Long getAc() {
        return ac;
    }

    public void setAc(Long ac) {
        this.ac = ac;
    }

    public Long getLc() {
        return lc;
    }

    public void setLc(Long lc) {
        this.lc = lc;
    }

    @Override
    public String toString() {
        return "id=" + id +
                "\nemail=" + em +
                "\nemailValidated=" + vl +
                "\nmobileSignup=" + mb +
                "\nfbLogin=" + fb +
                "\nsignupDate=" + sd +
                "\nlastLogin=" + ll +
                "\ntotalLogin=" + tl +
                "\nquestionsCount=" + qc +
                "\nanswersCount=" + ac +
                "\nlikesCount=" + lc;
    }
}

