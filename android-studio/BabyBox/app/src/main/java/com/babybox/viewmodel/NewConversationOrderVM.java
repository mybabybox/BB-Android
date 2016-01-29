package com.babybox.viewmodel;

public class NewConversationOrderVM {
    public Long conversationId;
    public double offeredPrice;

    public NewConversationOrderVM(Long conversationId, double offeredPrice) {
        this.conversationId = conversationId;
        this.offeredPrice = offeredPrice;
    }
}
