package com.softwire.training.shipit;

import com.softwire.training.shipit.builder.ProductBuilder;
import com.softwire.training.shipit.exception.ManifestCreationException;
import com.softwire.training.shipit.model.*;
import com.softwire.training.shipit.utils.OutboundOrderManifestCreator;
import junit.framework.TestCase;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.*;

public class OutboundManifestCreatorTest extends TestCase
{
    private static final int TRUCK_MAX_WEIGHT = 10;

    private OutboundOrderManifestCreator outboundOrderManifestCreator;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        outboundOrderManifestCreator = new OutboundOrderManifestCreator();
        outboundOrderManifestCreator.setTruckMaxWeightInGrammes(TRUCK_MAX_WEIGHT);
    }

    public void testFillSingleTruck() throws ManifestCreationException
    {
        OutboundOrderManifest manifest = createManifestFromItems(Arrays.asList(
                new ItemToPack("1", 1, 1),
                new ItemToPack("2", 2, 1),
                new ItemToPack("3", 3, 1),
                new ItemToPack("4", 4, 1)));

        verifyPacking(manifest, Collections.singletonList(Arrays.asList(
                new PackedItem("1", 1),
                new PackedItem("2", 2),
                new PackedItem("3", 3),
                new PackedItem("4", 4))));
    }

    public void testFillTwoTrucks() throws ManifestCreationException
    {
        OutboundOrderManifest manifest = createManifestFromItems(Arrays.asList(
                new ItemToPack("1", 2, 1),
                new ItemToPack("2", 2, 1),
                new ItemToPack("3", 3, 1),
                new ItemToPack("4", 4, 1)));

        verifyPacking(manifest, Arrays.asList(
                Arrays.asList(
                        new PackedItem("1", 2),
                        new PackedItem("3", 3),
                        new PackedItem("4", 4)),
                Collections.singletonList(
                        new PackedItem("2", 2))));
    }

    public void testNonUnitaryWeights() throws ManifestCreationException
    {
        OutboundOrderManifest manifest = createManifestFromItems(Arrays.asList(
                new ItemToPack("1", 2, 4.5f),
                new ItemToPack("2", 2, 0.1f),
                new ItemToPack("3", 3, 2f),
                new ItemToPack("4", 4, 1.001f)));

        verifyPacking(manifest, Arrays.asList(
                Arrays.asList(new PackedItem("1", 2), new PackedItem("2", 2)),
                Collections.singletonList(new PackedItem("3", 3)),
                Collections.singletonList(new PackedItem("4", 4))));
    }

    public void testCombinedWeightTooHeavy() throws ManifestCreationException
    {
        OutboundOrderManifest manifest = createManifestFromItems(Arrays.asList(
                new ItemToPack("1", 5, 4.5f),
                new ItemToPack("2", 1, 0.5f),
                new ItemToPack("3", 2, 0.5f)));

        verifyPacking(manifest, Arrays.asList(
                Arrays.asList(new PackedItem("1", 2), new PackedItem("3", 2)),
                Arrays.asList(new PackedItem("1", 2), new PackedItem("2", 1)),
                Collections.singletonList(new PackedItem("1", 1))));
    }

    public void testRoundWeightToKg() throws ManifestCreationException
    {
        outboundOrderManifestCreator.setTruckMaxWeightInGrammes(10 * 1000);
        OutboundOrderManifest manifest = createManifestFromItems(Collections.singletonList(
                new ItemToPack("1", 1, 4.51f * 1000f)));

        assertEquals(manifest.getTrucks().get(0).getWeightInKg(), 5);
    }

    public void testProductTooHeavy()
    {
        String gtin = "12345";
        try
        {
            createManifestFromItems(Collections.singletonList(new ItemToPack(gtin, 1, 11)));
            fail("Expected exception to be thrown");
        }
        catch (ManifestCreationException e)
        {
            assertTrue(e.getMessage().contains(gtin));
        }
    }


    private void verifyPacking(OutboundOrderManifest outboundOrderManifest, List<List<PackedItem>> expectedPacking)
    {
        assertEquals(expectedPacking.size(), outboundOrderManifest.getTrucks().size());

        for (int i = 0; i < outboundOrderManifest.getTrucks().size(); i++)
        {
            Truck truck = outboundOrderManifest.getTrucks().get(i);
            Set<PackedItem> expected = new HashSet<PackedItem>(expectedPacking.get(i));

            Set<PackedItem> actual = new HashSet<PackedItem>(truck.getOrderLines().size());
            for (SummaryOrderLine orderLine : truck.getOrderLines())
            {
                actual.add(new PackedItem(orderLine.getGtin(), orderLine.getQuantity()));
            }

            assertEquals(expected, actual);
        }
    }

    private OutboundOrderManifest createManifestFromItems(List<ItemToPack> items) throws ManifestCreationException
    {
        Map<String, Product> products = new HashMap<String, Product>(items.size());
        List<OrderLine> orderLines = new ArrayList<OrderLine>(items.size());
        OutboundOrder outboundOrder = new OutboundOrder(1, orderLines);

        for (ItemToPack item : items)
        {
            Product product = new ProductBuilder().setGtin(item.gtin).setWeight(item.weight).createProduct();
            orderLines.add(new OrderLine(item.gtin, item.quantity));
            products.put(item.gtin, product);
        }

        return outboundOrderManifestCreator.create(outboundOrder, products);
    }

    /**
     * To test the algorithm, for each product we only care about the weight quantity and ID of the products.
     */
    private static class ItemToPack
    {
        private final float weight;
        private final int quantity;
        private final String gtin;

        ItemToPack(String gtin, int quantity, float weight)
        {
            this.weight = weight;
            this.quantity = quantity;
            this.gtin = gtin;
        }
    }

    private static class PackedItem
    {
        private final int quantity;
        private final String gtin;

        PackedItem(String gtin, int quantity)
        {
            this.quantity = quantity;
            this.gtin = gtin;
        }

        @Override
        public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }

            if (o == null || getClass() != o.getClass())
            {
                return false;
            }

            PackedItem that = (PackedItem) o;

            return new EqualsBuilder()
                    .append(quantity, that.quantity)
                    .append(gtin, that.gtin)
                    .isEquals();
        }

        @Override
        public int hashCode()
        {
            return new HashCodeBuilder(17, 37)
                    .append(quantity)
                    .append(gtin)
                    .toHashCode();
        }

        @Override
        public String toString()
        {
            return new ToStringBuilder(this)
                    .append("gtin", gtin)
                    .append("quantity", quantity)
                    .toString();
        }
    }
}
