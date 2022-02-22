# Event-delivery Pattern Store-And-Forward


In this repository I have implemented a simple example of a store-and-forward pattern.
A client sends an event to the "forwarder" service that persists the event and returns OK.
A separate thread of execution in the forwarder picks up events that need to be processed
and attempts to process then. Events that are successfully processed are marked as such.
Events that fail to process have their attempts increased by 1 and can be retried again in 10 minutes.
If an event is picked up 3 times without success, the event is failed.


## What is the idea

Durably store received events such that they are not lost.
Try processing each event until it's processed at-least once.
Once delivered, mark it as delivered or delete it.

## When to use

If we need to send data to some service that can't pull messages of a
queue and can't poll for data. Furthermore, we need the data to be processed
at-least once.

## Things to consider

### Exponential backoff

If we fail to process an event, we could retry it with some exponential backoff
in the same thread of execution. However, this would block the thread until the retries
are exhausted.

We can instead implement this exponential backoff by setting the earliest time an event
can be processed based on how many times it has been attempted. Exponential backoff tends
to work better when we don't know how long the problem causing events to not process will take.

However, if we have stricter latency requirements, it might make more sense to go through
all retries early and then handle the event in another manner if possible.

Pseudo Code:
```
    onUnsuccessfulProcessing event -> 
        next_timestamp_offset = (1 << event.attempts) * <base backoff duration>
        setProcessableAgain(event, timstamp() + next_timestamp_offset) 
```
    
### Handling failed events

We want to fail events if we are unable to process them because they will
start using resources if the issues are not temporal. Why would an event fail?

* A failure to process an event could be due to schema changes making the event invalid.
* Could be that the service we are calling has a long outage. 
* Could be issues with the code preventing us from making a successful request.

Depending on how failed events should be handled, we have a couple of alternatives.

* Create a manual process for handling failed events. This should be avoided if possible.
* Drop failed events.
* If the failures are due to schema/code changes, create a periodic job that "patches" the broken events.
* Send the failed events to some other service that can handle them.