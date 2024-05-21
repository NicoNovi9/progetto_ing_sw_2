package model;

import returnStatus.TransactionStatus;

public record Transaction(int id, String applicantName, String district, String requestedLeaf, String offeredLeaf,
                          int requestedHours, int offeredHours, TransactionStatus status, String closerName) {
}
