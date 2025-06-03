package com.brasfi.platforma.domain;

import com.brasfi.platforma.model.User;

public record ChatInput(User user, String message) {
}
