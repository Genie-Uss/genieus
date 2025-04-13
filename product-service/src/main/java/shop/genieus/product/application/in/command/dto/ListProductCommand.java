package shop.genieus.product.application.in.command.dto;

import java.util.List;

public record ListProductCommand(List<Long> productIds) {}
