package com.microsoft.conference.registration.domain.SeatAssigning.Models;

import com.microsoft.conference.registration.domain.Orders.Models.OrderLine;
import com.microsoft.conference.registration.domain.SeatAssigning.Events.OrderSeatAssignmentsCreated;
import com.microsoft.conference.registration.domain.SeatAssigning.Events.SeatAssigned;
import com.microsoft.conference.registration.domain.SeatAssigning.Events.SeatUnassigned;
import org.enodeframework.common.exception.ArgumentException;
import org.enodeframework.common.exception.ArgumentOutOfRangeException;
import org.enodeframework.common.utilities.Ensure;
import org.enodeframework.common.utilities.Linq;
import org.enodeframework.common.utilities.ObjectId;
import org.enodeframework.domain.AggregateRoot;

import java.util.ArrayList;
import java.util.List;

public class OrderSeatAssignments extends AggregateRoot<String> {
    private String _orderId;
    private List<SeatAssignment> _assignments;

    public OrderSeatAssignments(String orderId, List<OrderLine> orderLines) {
        super(ObjectId.generateNewStringId());
        Ensure.notNullOrEmpty(orderId, "orderId");
        Ensure.notNull(orderLines, "orderLines");
        if (orderLines.isEmpty()) throw new ArgumentException("The seats of order cannot be empty.");
        int position = 0;
        List<SeatAssignment> assignments = new ArrayList<>();
        for (OrderLine orderLine : orderLines) {
            for (int i = 0; i < orderLine.SeatQuantity.Quantity; i++) {
                assignments.add(new SeatAssignment(position++, orderLine.SeatQuantity.Seat));
            }
        }
        applyEvent(new OrderSeatAssignmentsCreated(orderId, assignments));
    }

    public void AssignSeat(int position, Attendee attendee) {
        SeatAssignment current = Linq.singleOrDefault(_assignments, x -> x.Position == position);
        if (current == null) {
            throw new ArgumentOutOfRangeException("position");
        }
        if (current.attendee == null || attendee != current.attendee) {
            applyEvent(new SeatAssigned(current.Position, current.Seat, attendee));
        }
    }

    public void UnassignSeat(int position) {
        SeatAssignment current = Linq.singleOrDefault(_assignments, x -> x.Position == position);
        if (current == null) {
            throw new ArgumentOutOfRangeException("position");
        }
        applyEvent(new SeatUnassigned(position));
    }

    private void Handle(OrderSeatAssignmentsCreated evnt) {
        id = evnt.getAggregateRootId();
        _orderId = evnt.OrderId;
        _assignments = evnt.Assignments;
    }

    private void Handle(SeatAssigned evnt) {
        Linq.single(_assignments, x -> x.Position == evnt.Position).attendee = evnt.attendee;
    }

    private void Handle(SeatUnassigned evnt) {
        Linq.single(_assignments, x -> x.Position == evnt.Position).attendee = null;
    }
}
