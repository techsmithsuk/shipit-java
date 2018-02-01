package com.softwire.training.shipit.utils;

import com.softwire.training.shipit.exception.ManifestCreationException;
import com.softwire.training.shipit.model.*;

import java.util.*;

/**
 * Create manifest using first-fit bin-packing, picking items in an arbitrary order.
 * <p>
 * When possible, put multiple items of the same type in a single truck.  If it's not possible then split them up as
 * little as possible.
 */
public class OutboundOrderManifestCreator
{
    private int truckMaxWeightInGrammes = 2 * 1000 * 1000;  // 2000 Kg in grams

    public OutboundOrderManifest create(OutboundOrder outboundOrder, Map<String, Product> products)
            throws ManifestCreationException
    {
        List<Batch> batches = createBatches(outboundOrder, products);
        List<Truck> trucks = loadTrucks(batches);
        return new OutboundOrderManifest(trucks);
    }

    private List<Truck> loadTrucks(List<Batch> batches)
    {
        List<TruckBeingLoaded> trucksBeingLoaded = new ArrayList<TruckBeingLoaded>();

        for (Batch batch : batches)
        {
            SummaryOrderLine orderLine = new SummaryOrderLine(
                    batch.product.getGtin(), batch.product.getName(), batch.quantity);

            boolean orderLineHandled = false;

            for (TruckBeingLoaded truck : trucksBeingLoaded)
            {
                float spareWeight = truckMaxWeightInGrammes - truck.weightInGrammes;
                if (spareWeight >= batch.weightInGrammes)
                {
                    truck.addOrderLine(orderLine, batch.weightInGrammes);
                    orderLineHandled = true;
                    break;
                }
            }

            if (!orderLineHandled)
            {
                TruckBeingLoaded extraTruck = new TruckBeingLoaded();
                extraTruck.addOrderLine(orderLine, batch.weightInGrammes);
                trucksBeingLoaded.add(extraTruck);
            }
        }

        List<Truck> trucks = new ArrayList<Truck>();
        for (TruckBeingLoaded truckBeingLoaded : trucksBeingLoaded)
        {
            trucks.add(truckBeingLoaded.toTruck());
        }
        return trucks;
    }

    public void setTruckMaxWeightInGrammes(int truckMaxWeightInGrammes)
    {
        this.truckMaxWeightInGrammes = truckMaxWeightInGrammes;
    }

    private List<Batch> createBatches(
            OutboundOrder outboundOrder,
            Map<String, Product> products) throws ManifestCreationException
    {
        List<Batch> batches = new ArrayList<Batch>();
        for (OrderLine orderLine : outboundOrder.getOrderLines())
        {
            Product product = products.get(orderLine.getGtin());
            if (product.getWeight() > truckMaxWeightInGrammes)
            {
                throw new ManifestCreationException(String.format(
                        "Product %s is too heavy to be packed into a single truck", product));
            }

            int productsPerTruck = (int) Math.floor(((double) truckMaxWeightInGrammes) / product.getWeight());

            int numCompleteBatches = (int) Math.floor(orderLine.getQuantity() / productsPerTruck);
            int finalBatchSize = orderLine.getQuantity() % productsPerTruck;

            for (int i = 0; i < numCompleteBatches; i++)
            {
                batches.add(new Batch(product, productsPerTruck));
            }
            if (finalBatchSize > 0)
            {
                batches.add(new Batch(product, finalBatchSize));
            }
        }
        Collections.sort(batches, new Comparator<Batch>()
        {
            public int compare(Batch o1, Batch o2)
            {
                return -Float.compare(o1.weightInGrammes, o2.weightInGrammes);
            }
        });
        return batches;
    }

    /**
     * Batch of products which will be loaded onto a single truck
     */
    private class Batch
    {
        private final Product product;
        private final int quantity;
        private final float weightInGrammes;

        Batch(Product product, int quantity)
        {
            this.product = product;
            this.quantity = quantity;
            this.weightInGrammes = product.getWeight() * quantity;
        }
    }

    private class TruckBeingLoaded
    {
        private float weightInGrammes;
        private List<SummaryOrderLine> orderLines;

        TruckBeingLoaded()
        {
            this.weightInGrammes = 0;
            this.orderLines = new ArrayList<SummaryOrderLine>();
        }

        void addOrderLine(SummaryOrderLine orderLine, float weightInGrammes)
        {
            orderLines.add(orderLine);
            this.weightInGrammes += weightInGrammes;
        }

        Truck toTruck()
        {
            return new Truck(this.orderLines, (int) Math.ceil(weightInGrammes / 1000));
        }
    }
}
