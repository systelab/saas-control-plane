package com.systelab.saas.service.aws;

public enum AMI {
    AMAZON_LINUX2_AMI("ami-09def150731bdbcc2");

    private final String amiID;

    AMI(final String amiID) {
        this.amiID = amiID;
    }

    @Override
    public String toString() {
        return amiID;
    }
}
