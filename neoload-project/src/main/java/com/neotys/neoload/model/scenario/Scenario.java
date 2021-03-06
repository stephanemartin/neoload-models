package com.neotys.neoload.model.scenario;

import org.immutables.value.Value;

import java.util.Map;

@Value.Immutable
public interface Scenario {

    String getName();
    Map<String, ScenarioPolicies> getPopulations();
}
