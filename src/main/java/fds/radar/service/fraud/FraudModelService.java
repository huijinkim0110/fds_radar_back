package fds.radar.service.fraud;

import fds.radar.service.fraud.vo.FraudPrediction;
import fds.radar.service.fraud.vo.TransactionData;

public interface FraudModelService {
    FraudPrediction predict(TransactionData transactionData);
}