package com.example.eventify.validation;

import jakarta.validation.groups.Default;

public interface ValidationGroups {
    interface OnCreate extends Default {}
    interface OnUpdate extends Default {}
}