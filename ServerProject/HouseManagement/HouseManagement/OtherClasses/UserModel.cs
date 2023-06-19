namespace HouseManagement.OtherClasses
{
    public class UserModel
    {
        public string? id { get; set; }
        public string email { get; set; }
        public string password { get; set; }
        public string? role { get; set; }
        public int? idFlatOwner { get; set; }
        public List<int>? idHouses { get; set; }
        public UserModel() { }
        public UserModel(string id, string email, string password, string role, int? idFlatOwner, List<int>? idHouses)
        {
            this.id = id;
            this.email = email;
            this.password = password;
            this.role = role;
            this.idFlatOwner = idFlatOwner;
            this.idHouses = idHouses;
        }
    }
}
