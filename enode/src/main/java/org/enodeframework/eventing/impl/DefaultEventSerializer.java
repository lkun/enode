package org.enodeframework.eventing.impl;

import com.google.common.collect.Maps;
import org.enodeframework.common.serializing.JsonTool;
import org.enodeframework.eventing.IDomainEvent;
import org.enodeframework.eventing.IEventSerializer;
import org.enodeframework.infrastructure.ITypeNameProvider;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author anruence@gmail.com
 */
public class DefaultEventSerializer implements IEventSerializer {
    @Autowired
    private ITypeNameProvider typeNameProvider;

    public DefaultEventSerializer setTypeNameProvider(ITypeNameProvider typeNameProvider) {
        this.typeNameProvider = typeNameProvider;
        return this;
    }

    @Override
    public Map<String, String> serialize(List<IDomainEvent> evnts) {
        LinkedHashMap<String, String> dict = Maps.newLinkedHashMap();
        evnts.forEach(evnt -> {
            String typeName = typeNameProvider.getTypeName(evnt.getClass());
            String eventData = JsonTool.serialize(evnt);
            dict.put(typeName, eventData);
        });
        return dict;
    }

    @Override
    public <TEvent extends IDomainEvent> List<TEvent> deserialize(Map<String, String> data, Class<TEvent> domainEventType) {
        List<TEvent> evnts = new ArrayList<>();
        data.entrySet().forEach(entry -> {
            Class eventType = typeNameProvider.getType(entry.getKey());
            TEvent evnt = (TEvent) JsonTool.deserialize(entry.getValue(), eventType);
            evnts.add(evnt);
        });
        return evnts;
    }
}
