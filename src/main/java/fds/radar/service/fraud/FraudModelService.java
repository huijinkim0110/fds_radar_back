package fds.radar.service.fraud;

public interface FraudModelService {
    FraudPrediction predict(TransactionData transactionData);
}