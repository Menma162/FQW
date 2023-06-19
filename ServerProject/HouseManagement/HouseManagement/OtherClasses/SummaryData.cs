namespace HouseManagement.OtherClasses
{
    public class SummaryData
    {
        public string nameHouse { get; set; }
        public string nameService { get; set; }
        public int summa { get; set; }
        public string unit { get; set; }

        public SummaryData(string nameService, string nameHouse, int summa, string unit)
        {
            this.nameHouse = nameHouse;
            this.nameService = nameService;
            this.summa = summa;
            this.unit = unit;
        }
    }
}
