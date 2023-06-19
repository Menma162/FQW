namespace HouseManagement.OtherClasses
{
    public class Photo
    {
        int id { get; set; }
        string? array { get; set; }
        public Photo(int id, string array) 
        {
            this.id = id;
            this.array = array;
        }
    }
}
